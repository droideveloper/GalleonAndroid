/*
 * Galleon Copyright (C) 2016 Fatih.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fs.galleon.managers;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java8.util.stream.StreamSupport;
import org.fs.common.AbstractManager;
import org.fs.exception.AndroidException;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.entities.Document;
import org.fs.galleon.entities.Syncable;
import org.fs.galleon.nets.ICloud;
import org.fs.galleon.nets.IEndpoint;
import org.fs.util.PreconditionUtility;
import org.fs.util.StringUtility;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class FileManager extends AbstractManager implements IFileManager {

  private final static long   INTERVAL = 60;
  private final static String ANY_FILE = "*.*";

  private final static String GZ_EXTENSION  = ".gz";
  private final static String GZ_FORMAT     = "%s.%s";
  private final static int BUFFER_SIZE      = 8192;

  private Subscription sync;

  private final File defaultDirectory;
  private final IPrefManager prefManager;
  private final IDatabaseManager dbManager;
  private final IEndpoint endpoint;
  private final ICloud cloud;

  public FileManager(Context context, IPrefManager prefManager, IDatabaseManager dbManager, IEndpoint endpoint, ICloud cloud) {
    PreconditionUtility.checkNotNull(context, "context is null.");
    PreconditionUtility.checkNotNull(prefManager, "PreferenceManager is null.");
    this.defaultDirectory = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
    this.prefManager = prefManager;
    this.dbManager = dbManager;
    this.endpoint = endpoint;
    this.cloud = cloud;
  }

  @Override public Observable<File> createIfNotExists(String directory) {
    return Observable.just(directory)
                     .flatMap(x -> {
                       File dir = new File(directory);
                       if(!dir.exists()) {
                         boolean success = dir.mkdirs();
                         if(!success) {
                           throw new AndroidException(
                               String.format(Locale.ENGLISH, "failed to create directory '%s'.", directory));
                         }
                       }
                       return Observable.just(dir);
                     });
  }

  @Override public Observable<File> createIfNotExists(File directory) {
    return Observable.just(directory)
              .flatMap(x -> {
                if (!x.exists()) {
                  boolean success = x.mkdirs();
                  if (!success) {
                    throw new AndroidException(
                        String.format(Locale.ENGLISH, "failed to create directory '%s'.", directory));
                  }
                }
                return Observable.just(x);
              });
  }

  @Override public Observable<File> combinePath(String directory, String append) {
    return Observable.just(new File(directory, append));
  }

  @Override public Observable<File> combinePath(File directory, String append) {
    return Observable.just(new File(directory, append));
  }

  @Override public Observable<File> toCompressedFile(File in) {
    if(!in.exists()) {
      return Observable.empty();
    }
    return toGzipFile(in).flatMap(x -> {
      FileInputStream input = null;
      try {
        int cursor;
        byte[] buffer = new byte[BUFFER_SIZE];
        input = new FileInputStream(in);
        GZIPOutputStream gzip = new GZIPOutputStream(new FileOutputStream(x));
        while((cursor = input.read(buffer)) != -1) {
          gzip.write(buffer, 0, cursor);
        }
        gzip.close();//close this for change written
        return Observable.just(x);
      } catch (Exception error) {
        throw new AndroidException(error);
      } finally {
        try {
          if(input != null) {
            input.close();
          }
        } catch (IOException error) {
          log(error);
        }
      }
    });
  }

  @Override public Observable<File> toDecompressedFile(File file, GZIPInputStream input) {
    if(file.exists()) {
      boolean success = file.delete();
      if (success) {
        log(Log.INFO,
            String.format(Locale.getDefault(), "'%s' is deleted.", file.getAbsolutePath())
        );
      }
    }
    return Observable.just(file)
            .flatMap(x -> {
              try {
                int cursor;
                byte[] buffer = new byte[BUFFER_SIZE];
                FileOutputStream output = new FileOutputStream(x);
                while((cursor = input.read(buffer)) != -1) {
                  output.write(buffer, 0, cursor);
                }
                output.close();
                input.close();
                return Observable.just(x);
              } catch (Exception error) {
                throw new AndroidException(error);
              }
            });
  }

  @Override public Observable<File> toGzipFile(File file) {
    return Observable.just(file)
              .flatMap(x -> {
                final String filename = String.format(Locale.ENGLISH, GZ_FORMAT, x.getName(), GZ_EXTENSION);
                String filePath = x.getAbsolutePath();
                filePath = filePath.substring(0, filePath.lastIndexOf(File.pathSeparator));
                return Observable.just(new File(filePath, filename));
              });
  }

  @Override public File defaultDirectory() {
    return this.defaultDirectory;
  }

  @Override public File syncDirectory() {
    String syncPath = prefManager.getValue(PrefManager.KEY_SYNC_FOLDER, StringUtility.EMPTY);
    if (!StringUtility.isNullOrEmpty(syncPath)) {
      File syncDirectory = new File(defaultDirectory, syncPath);
      if (!syncDirectory.exists()) {
        boolean success = syncDirectory.mkdirs();
        if (success) {
          log(Log.INFO,
              String.format(Locale.getDefault(), "Sync Path as '%s' created.", syncDirectory.getAbsolutePath())
          );
        }
      }
      return syncDirectory;
    }
    return null;
  }

  @Override public File toolsDirectory() {
    String toolsPath = prefManager.getValue(PrefManager.KEY_TOOLS_FOLDER, StringUtility.EMPTY);
    if (!StringUtility.isNullOrEmpty(toolsPath)) {
      File toolsDirectory = new File(defaultDirectory, toolsPath);
      if (!toolsDirectory.exists()) {
        boolean success = toolsDirectory.mkdirs();
        if (success) {
          log(Log.INFO,
              String.format(Locale.getDefault(), "Tools Path as '%s' created.", toolsDirectory.getAbsolutePath())
          );
        }
      }
      return toolsDirectory;
    }
    return null;
  }

  @Override public List<File> searchFiles(File directory, SearchOptions options, String fileExtension) {
    return findFiles(directory, options, fileExtension);
  }

  @Override public void start() {
    if (sync == null) {
      sync = dispatchSyncInterval();
    } else {
      log(Log.WARN,
          String.format(Locale.getDefault(), "Sync is already running. %s",
              new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(new Date()))
      );
    }
  }

  @Override public void stop() {
    if (sync != null) {
      sync.unsubscribe();
      sync = null;
    }
  }

  @Override protected String getClassTag() {
    return FileManager.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }

  private List<File> findFiles(File directory, SearchOptions options, String extension) {
    Pattern pattern = toPattern(extension);
    List<File> files = new ArrayList<>();
    files.addAll(
        Arrays.asList(
            directory.listFiles(f -> f.isFile() && isMatch(pattern, f.getName()))));
    if (options.equals(SearchOptions.ALL_DIRECTORIES)) {
      StreamSupport.stream(
          Arrays.asList(directory.listFiles(File::isDirectory)))
          .forEach(dir ->
            files.addAll(findFiles(dir, options, extension)));
    }
    return files;
  }

  //"([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)"
  // original pattern compiled from
  private Pattern toPattern(String nameAndExtension) {
    StringBuilder patternBuilder = new StringBuilder();
    patternBuilder.append('(');
    String name = nameAndExtension.substring(0, nameAndExtension.lastIndexOf('.'));
    String extension = nameAndExtension.substring(nameAndExtension.lastIndexOf('.'));
    if (name.indexOf('*') != -1) {
      if (name.length() == 1) {
        patternBuilder.append("[^\\s]+(\\.(?i)");
      } else {
        String expected = extension.substring(extension.indexOf('*'));
        if (!StringUtility.isNullOrEmpty(expected)) {
          patternBuilder.append("(" + expected + ")+[^\\s]+(\\.(?i)");
        } else {
          expected = extension.substring(extension.indexOf('*'), expected.length());
          patternBuilder.append("[^\\s]+(" + expected + ")+(\\.(?i))");
        }
      }
    }
    if (extension.indexOf('*') != -1) {
      if (extension.length() == 1) {
        patternBuilder.append("([^\\s]))$)");
      } else {
        String expected = extension.substring(extension.indexOf('*'));
        if (!StringUtility.isNullOrEmpty(expected)) {
          patternBuilder.append("(" + expected +")+[^\\s]))$)");
        } else {
          expected = extension.substring(extension.indexOf('*'), expected.length());
          patternBuilder.append("([^\\s]+(" + expected + ")))$)");
        }
      }
    }
    log(Log.INFO,
      String.format(Locale.getDefault(), "%s", patternBuilder.toString())
    );
    return Pattern.compile(patternBuilder.toString());
  }

  private boolean isMatch(Pattern pattern, String filename) {
    Matcher matcher = pattern.matcher(filename);
    return matcher.matches();
  }

  private Subscription dispatchSyncInterval() {
    return Observable.interval(INTERVAL, TimeUnit.SECONDS)
        .flatMap(time -> {
          boolean isAutoSync = prefManager.getValue(PrefManager.KEY_AUTO_SYNC, false);
          if (isAutoSync) {
            return dbManager.everything();
          }
          return Observable.empty();
        })
        .flatMap(Observable::from)
        .flatMap(x -> {
          File syncDirectory = syncDirectory();
          if (syncDirectory == null) return Observable.empty();
          if (syncDirectory.exists()) {
            return Observable.just(findFiles(syncDirectory, SearchOptions.ALL_DIRECTORIES, ANY_FILE))
                .flatMap(Observable::from)
                .filter(y -> y.getAbsolutePath().equalsIgnoreCase(x.getLocalPath()))
                .map(y -> new Match(x.getLastModifiedTime().getTime() != y.lastModified() ? Operation.UPLOAD : Operation.NONE, y, x, null))
                .firstOrDefault(null);
          }
          return Observable.empty();
        })
        .flatMap(x -> {
          if (x.operation.equals(Operation.NONE)) {
            return endpoint.queryDocument(x.sync.getRemoteId())
                .flatMap(y -> {
                  if (y.isSuccess()) {
                    Document docx = y.data();
                    if(!StringUtility.isNullOrEmpty(docx)) {
                      return Observable.just(new Match(Operation.DELETE, x.file, x.sync, docx));
                    } else {
                      return Observable.just(new Match(x.sync.getLastModifiedTime() != docx.getUpdateDate() ? Operation.DOWNLOAD : Operation.NONE, x.file, x.sync, docx));
                    }
                  }
                  return Observable.just(x);
                });
          }
          return Observable.just(x);
        })
        .flatMap(x -> {
          if (x.operation.equals(Operation.DELETE)) {
            dbManager.remove(x.sync.getSyncableId());
            if (x.file.exists()) {
              boolean success = x.file.delete();
              if (success) {
                log(Log.INFO,
                    String.format(Locale.getDefault(), "%s file deleted by sync manager.", x.file.getAbsolutePath())
                );
              }
            }
            return Observable.just(new Pair(x, true));
          } else if (x.operation.equals(Operation.DOWNLOAD)) {
            if (x.file.exists()) {
              boolean success = x.file.delete();
              if (success) {
                log(Log.INFO,
                    String.format(Locale.getDefault(), "%s file deleted by sync manager for download.", x.file.getAbsolutePath())
                );
              }
            }
            return cloud.downloadContent(x.sync.getRemoteId())
                .flatMap(body -> {
                  try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.US);
                    File dir = new File(defaultDirectory(), "/dcache");
                    if (!dir.exists()) {
                      boolean success = dir.mkdirs();
                      if (success) {
                        log(Log.INFO,
                          String.format(Locale.ENGLISH, "%s is created", dir.getAbsolutePath())
                        );
                      }
                    }
                    File file = new File(dir, "temp_" + format.format(new Date()) + ".gz");
                    byte[] buffer = new byte[8192];
                    OutputStream out = new FileOutputStream(file);
                    InputStream input = body.byteStream();
                    int read;
                    while((read = input.read(buffer)) != -1) {
                      out.write(buffer, 0, read);
                    }
                    input.close();
                    out.flush();
                    out.close();
                    return Observable.just(file);
                  } catch (Exception error) {
                    throw new AndroidException(error);
                  }
                })
                .flatMap(f -> {
                  try {
                    return toDecompressedFile(x.file, new GZIPInputStream(new FileInputStream(f)));
                  } catch (IOException ioError) {
                    log(ioError);
                    throw new AndroidException(ioError);
                  } finally {
                    if (f.exists()) {
                      boolean success = f.delete();
                      if (success) {
                        log(Log.INFO,
                            String.format(Locale.getDefault(), "%s temp file deleted after download.", f.getAbsolutePath())
                        );
                      }
                    }
                  }
                })
                .flatMap(f -> {
                  boolean success = f.setLastModified(x.docx.getUpdateDate().getTime());
                  if(success) {
                    log(Log.INFO,
                        String.format(Locale.getDefault(), "%s date is set.", f.getAbsolutePath())
                    );
                  }
                  x.sync = x.sync.newBuilder()
                      .lastModifiedTime(x.docx.getUpdateDate())
                      .localPath(f.getAbsolutePath())
                      .remoteId(x.docx.getDocumentId())
                      .build();
                  return dbManager.save(x.sync);
                })
                .flatMap(s -> Observable.just(new Pair(new Match(x.operation, new File(s.getLocalPath()), s, x.docx), true)));
          } else if (x.operation.equals(Operation.UPLOAD)) {
            return toCompressedFile(x.file)
                .flatMap(y -> {
                  Document docx = x.docx.newBuilder()
                      .updateDate(new Date(x.file.lastModified()))
                      .contentLength(x.file.length())
                      .build();
                  //update Document object
                  return endpoint.updateDocument(docx)
                      .flatMap(response -> {
                        if (response.isSuccess()) {
                          return Observable.just(response.data());
                        }
                        return Observable.empty();
                      })
                      //download upload content to server
                      .flatMap(z -> cloud.updateContent(z.getDocumentId(), y)
                          .flatMap(n -> {
                            if (y.exists()) {
                              boolean success = y.delete();
                              if(success) {
                                log(Log.INFO,
                                    String.format(Locale.getDefault(), "compressed file is deleted %s", y.getAbsolutePath())
                                );
                              }
                            }
                            return Observable.just(new Pair(x, n.data()));
                          }));
                });

          } else {
            return Observable.just(new Pair(x, false));
          }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(x -> {
          if (x.state) {
            log(Log.INFO,
                String.format(Locale.getDefault(), "sync operation %s with file: %s and state %s",
                    String.valueOf(x.match.operation),
                    x.match.file.getAbsolutePath(),
                    String.valueOf(x.state)));
          }
        }, this::log);
  }

  private class Pair {
    Match match;
    boolean state;

    Pair(Match match, boolean state) {
      this.match = match;
      this.state = state;
    }
  }

  private class Match {
    Operation operation;
    File file;
    Syncable sync;
    Document docx;
    Match(Operation operation, File file, Syncable sync, Document docx) {
      this.operation = operation;
      this.file = file;
      this.sync = sync;
      this.docx = docx;
    }
  }

  private enum Operation {
    UPLOAD,
    DOWNLOAD,
    DELETE,
    NONE
  }
}