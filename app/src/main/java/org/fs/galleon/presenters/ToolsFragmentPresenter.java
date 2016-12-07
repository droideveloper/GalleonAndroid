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
package org.fs.galleon.presenters;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import com.googlecode.leptonica.android.AdaptiveMap;
import com.googlecode.leptonica.android.GrayQuant;
import com.googlecode.leptonica.android.ImageFormat;
import com.googlecode.leptonica.android.MorphApp;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.Rotate;
import com.googlecode.leptonica.android.Skew;
import com.jakewharton.rxbinding.view.RxView;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java8.util.stream.Collectors;
import java8.util.stream.IntStreams;
import java8.util.stream.StreamSupport;
import org.fs.common.AbstractPresenter;
import org.fs.common.BusManager;
import org.fs.common.ThreadManager;
import org.fs.exception.AndroidException;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.R;
import org.fs.galleon.commons.SimpleCallbackImp;
import org.fs.galleon.entities.ImageEntity;
import org.fs.galleon.events.SingleImageSelectionEvent;
import org.fs.galleon.events.TitleEvent;
import org.fs.galleon.managers.IFileManager;
import org.fs.galleon.utils.Images;
import org.fs.galleon.views.IToolsFragmentView;
import org.fs.util.APICompats;
import org.fs.util.Collections;
import org.fs.util.ObservableList;
import org.fs.util.StringUtility;
import org.pdf.haru.PdfCore;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ToolsFragmentPresenter extends AbstractPresenter<IToolsFragmentView>
    implements IToolsFragmentPresenter, SimpleCallbackImp.SwipeCallback {

  private final static int PERMISSION_READ_STORAGE = 0x09;

  private final static int REQUEST_TAKE_PHOTO   = 0x01;
  private final static int REQUEST_PICK_GALLERY = 0x02;

  private final static String FILE_PDF_EXTENSION = ".pdf";//need to create pdf extension here, might be required for new native api
  private final static String FILE_EXTENSION = ".jpg";
  private final static String FILE_PREFIX = "JPEG";
  private final static String FILE_STAMP = "yyyyMMdd_HHmmssSSS";

  private final static String IMAGE_TYPE = "image/*";

  private final static String GRANT_PERMISSION = "org.fs.galleon.fileprovider";

  private final static SimpleDateFormat timeStamp = new SimpleDateFormat(FILE_STAMP,
      Locale.getDefault());

  private final static String KEY_TEMP_TAKEN_PHOTO = "temp.taken.photo.file";
  private final static String KEY_PDF_FILES = "pdf.files";
  private final static String KEY_IMAGE_ENTITIES = "image.entities";

  private Subscription viewProcessSub;
  private Subscription eventListener;

  private File tempTakenPhoto;
  private ObservableList<File> pdfs;
  private List<ImageEntity> images;

  private final IFileManager fileManager;

  public ToolsFragmentPresenter(IToolsFragmentView view, IFileManager fileManager) {
    super(view);
    this.fileManager = fileManager;
  }

  @Override public void onStart() {
    if (view.isAvailable()) {
      String titleStr = view.getContext().getString(R.string.titleNavigationTools);
      BusManager.send(new TitleEvent(titleStr));
      if (!Collections.isNullOrEmpty(images)) {
        view.clearAllAdapters();//it will overlap same date over and over again if we do not clear previous
      }
      if (!Collections.isNullOrEmpty(images)) {
        view.bindRecyclerAdapter(images);
      }
      if (pdfs == null) {
        pdfs = new ObservableList<>();
      }
    }
    eventListener = BusManager.add((event) -> {
      if (event instanceof SingleImageSelectionEvent) {
        SingleImageSelectionEvent e = (SingleImageSelectionEvent) event;
        if (e.entity() != null) {
          ImageEntity entity = e.entity();
          File file = new File(entity.getImageUri().getPath());
          String f = file.getName();
          final String fx = f.substring(0, f.lastIndexOf('.'));
          if (pdfs != null) {
            StreamSupport.stream(pdfs)
                .filter(x -> {
                  String filename = x.getName();
                  filename = filename.substring(0, filename.lastIndexOf('.'));
                  return filename.equalsIgnoreCase(fx);
                })
                .forEach(x -> {
                  if (view.isAvailable()) {
                    view.setFileTitle(x.getName());
                    view.setPdfFile(Uri.fromFile(x));
                  }
                });
          }
        }
      }
    });
    dispatchAskPermissionsIfNeeded();
  }

  @Override public void onStop() {
    if (viewProcessSub != null) {
      viewProcessSub.unsubscribe();
      viewProcessSub = null;
    }
    if (eventListener != null) {
      BusManager.remove(eventListener);
      eventListener = null;
    }
  }

  @Override public void restoreState(Bundle restoreState) {
    if (restoreState != null) {
      if (restoreState.containsKey(KEY_TEMP_TAKEN_PHOTO)) {
        String absolutePath = restoreState.getString(KEY_TEMP_TAKEN_PHOTO);
        if (!StringUtility.isNullOrEmpty(absolutePath)) {
          tempTakenPhoto = new File(absolutePath);
        }
      }
      if (restoreState.containsKey(KEY_IMAGE_ENTITIES)) {
        images = restoreState.getParcelableArrayList(KEY_IMAGE_ENTITIES);
      }
      if (restoreState.containsKey(KEY_PDF_FILES)) {
        List<String> absolutePaths = restoreState.getStringArrayList(KEY_PDF_FILES);
        if(!Collections.isNullOrEmpty(absolutePaths)) {
          List<File> fileList = StreamSupport.stream(absolutePaths)
              .map(File::new)
              .collect(Collectors.toList());
          pdfs.addAll(fileList);
        }
      }
    }
  }

  @Override public void storeState(Bundle storeState) {
    if (tempTakenPhoto != null) {
      storeState.putString(KEY_TEMP_TAKEN_PHOTO, tempTakenPhoto.getAbsolutePath());
    }
    if (!Collections.isNullOrEmpty(images)) {
      storeState.putParcelableArrayList(KEY_IMAGE_ENTITIES, new ArrayList<>(images));
    }
    if (!Collections.isNullOrEmpty(pdfs)) {
      List<String> absolutePaths = StreamSupport.stream(pdfs)
                                                .map(File::getAbsolutePath)
                                                .collect(Collectors.toList());
      storeState.putStringArrayList(KEY_PDF_FILES, new ArrayList<>(absolutePaths));
    }
  }

  @Override public void observeRecyclerView(RecyclerView viewRecycler) {
    ItemTouchHelper touchHelper = new ItemTouchHelper(SimpleCallbackImp.swipeEndHelper(this));
    touchHelper.attachToRecyclerView(viewRecycler);
  }

  @Override public void observeProcessView(FloatingActionButton viewProcess) {
    viewProcessSub = RxView.clicks(viewProcess)
        .subscribe(click -> {
          if (view.isAvailable()) {
            if (Collections.isNullOrEmpty(images)) return;
            view.showProgress();
            dispatchImageToPdfProcess()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                  if(view.isAvailable()) {
                    pdfs.addAll(data);
                  }
                }, this::log, view::hideProgress);
          }
        });
  }

  @Override public void activityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_PICK_GALLERY) {
      if (resultCode == Activity.RESULT_OK) {
        ClipData clipData = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
          clipData = data.getClipData();
        }
        if (clipData != null) {
         //multi choice
         List<ImageEntity> items =  IntStreams.range(0, clipData.getItemCount())
              .mapToObj(clipData::getItemAt)
              .map(item -> Images.getPath(view.getContext(), item.getUri()))
              .map(xPath -> new ImageEntity.Builder().imageUri(Uri.fromFile(new File(xPath))).build())
              .collect(Collectors.toList());

          createIfImageListNotExists();
          if (!Collections.isNullOrEmpty(items)) {
            images.addAll(items);
          }

        } else {
          //single choice
          Uri uri = data.getData();
          if (uri != null) {
            String path = Images.getPath(view.getContext(), uri);
            ImageEntity entity = new ImageEntity.Builder()
                .imageUri(Uri.fromFile(new File(path)))
                .build();
            createIfImageListNotExists();
            images.add(entity);
          }
        }
      }
    } else if (requestCode == REQUEST_TAKE_PHOTO){
      if (resultCode == Activity.RESULT_OK) {
        if(tempTakenPhoto != null && tempTakenPhoto.exists()) {
          ImageEntity entity = new ImageEntity.Builder()
              .imageUri(Uri.fromFile(tempTakenPhoto))
              .build();
          //I might need to use orientation data and etc.
          createIfImageListNotExists();//create if its null
          if (view.isAvailable()) {
            images.add(entity);
          }
          tempTakenPhoto = null;//clear it now
        }
      }
    }
  }

  @Override public boolean optionsMenuSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.actionTakePhoto: {
        dispatchTakePictureIntent();
        return true;
      }
      case R.id.actionPickGallery: {
        dispatchSelectGalleryIntent();
        return true;
      }
      case R.id.actionClearAll: {
        dispatchClearAll();
        return true;
      }
      default:
        return false;
    }
  }

  @Override public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    if (view.isAvailable()) {
      int adapterPosition = viewHolder.getAdapterPosition();
      ImageEntity entity = view.removeAt(viewHolder.getAdapterPosition());
      String titleStr = view.getContext().getString(R.string.titleDeleteEntity);
      String okStr = view.getContext().getString(android.R.string.ok);
      titleStr = String.format(Locale.getDefault(), titleStr, okStr, toEntityName(entity));
      view.showError(titleStr, okStr,
          (v) -> view.addAt(adapterPosition, entity));
    }
  }

  @Override public void requestPermissions(int requestCode, String[] permissions, int[] results) {
    //I do not require need for this
  }

  @Override protected String getClassTag() {
    return ToolsFragmentPresenter.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }

  private Observable<File> createIfNotExists() {
    return Observable.just(toImageFileName())
          .flatMap(fileName -> {
            if (view.isAvailable()) {
              try {
                final Context context = view.getContext();
                File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File temp = new File(storageDir, fileName + FILE_EXTENSION);
                boolean success = temp.createNewFile();
                if (success) {
                  log(Log.INFO,
                      String.format(Locale.ENGLISH, "%s is temp created", temp.getName())
                  );
                }
                return Observable.just(temp);
              } catch (IOException ioError) {
                throw new AndroidException(ioError);
              }
            }
            return Observable.empty();
          });
  }

  private void dispatchTakePictureIntent() {
    createIfNotExists()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(file -> {
          if (file.exists()) {
            log(Log.INFO,
                String.format(Locale.ENGLISH, "%s is temp file.", file.getAbsolutePath())
            );
          }
          if (view.isAvailable()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(view.getContext().getPackageManager()) != null) {
              this.tempTakenPhoto = file;//set it in property
              Uri uri = FileProvider.getUriForFile(view.getContext(), GRANT_PERMISSION, file);
              intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
              //fileProvider requires gran permission to others access that uri
              if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                final Context context = view.getContext();
                List<ResolveInfo> infos = context.getPackageManager()
                                                 .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (infos != null) {
                  StreamSupport.stream(infos)
                      .filter(x -> x.activityInfo != null)
                      .map(x -> x.activityInfo.packageName)
                      .forEach(pack -> {
                        context.grantUriPermission(pack, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                      });
                }
              }
              view.startActivityForResult(intent, REQUEST_TAKE_PHOTO);
            } else {
              view.showError("You need to install app that can capture photo.");
            }
          }
        }, this::log);
  }

  private void dispatchClearAll() {
    if (view.isAvailable()) {
      //if we have items that needs to be cleared but before that icon is black
      if (!Collections.isNullOrEmpty(pdfs)
          || !Collections.isNullOrEmpty(images)) {
        //clean adapters
        view.clearAllAdapters();
        //delete those from file system
        StreamSupport.stream(pdfs).filter(File::exists).forEach(File::delete);
        pdfs.clear();
        images.clear();
        tempTakenPhoto = null;
      }
    }
  }

  private Observable<List<File>> dispatchImageToPdfProcess() {
    return Observable.just(images)
        .flatMap(Observable::from)
        .map(uri -> new File(uri.getImageUri().getPath()))
        .map(file -> {
          File toolsDirectory = fileManager.toolsDirectory();
          if (toolsDirectory == null) {
            ThreadManager.runOnUiThread(() -> {
              if (view.isAvailable()) {
                view.showError("You should picked a tools directory for working with img to pdf conversions.");
              }
            });
            return null;
          }
          String newFileName = file.getName();
          newFileName = newFileName.substring(0, newFileName.lastIndexOf('.'));
          File jpegFile = new File(fileManager.toolsDirectory(), newFileName + FILE_EXTENSION);
          File pdfFile = new File(fileManager.toolsDirectory(), newFileName + FILE_PDF_EXTENSION);

          Pix pixs = ReadFile.readFile(file);
          pixs = AdaptiveMap.backgroundNormSimple(pixs);
          pixs = pixs.convertRGBToGray();
          float angle = Skew.findSkew(pixs);
          pixs = Rotate.rotate(pixs, angle, true, true);
          pixs = MorphApp.pixTophat(pixs);
          pixs = MorphApp.pixInvert(pixs);
          pixs = GrayQuant.pixGammaRTC(pixs);
          pixs = GrayQuant.pixThresholdToBinary(pixs);
          pixs.write(jpegFile, ImageFormat.JfifJpg);

          PdfCore.createFromJpeg(jpegFile, pixs.getWidth(), pixs.getHeight(), pdfFile);
          if (pdfFile.exists()) {
            log(Log.WARN,
              String.format(Locale.ENGLISH, "%s named file has size of %d", pdfFile.getName(), pdfFile.length())
            );
          }
          pixs.recycle();
          return pdfFile;
        })
        .filter(f -> !StringUtility.isNullOrEmpty(f))
        .toList();
  }

  private void dispatchSelectGalleryIntent() {
    if (view.isAvailable()) {
      Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
      intent.setType(IMAGE_TYPE);
      if (APICompats.isApiAvailable(Build.VERSION_CODES.JELLY_BEAN_MR2)) {
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
      }
      view.startActivityForResult(
          Intent.createChooser(intent, view.getContext().getString(R.string.app_name)),
          REQUEST_PICK_GALLERY);
    }
  }

  private void dispatchAskPermissionsIfNeeded() {
    if (view.isAvailable()) {
      if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        if (view.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
          AlertDialog dialog = new AlertDialog.Builder(view.getContext())
              .setCancelable(true)
              .setTitle(R.string.titleReadImagesPermission)
              .setMessage(R.string.titleReadImagesPermissionMessage)
              .setNegativeButton(android.R.string.cancel, (d, w) -> log(String.format(Locale.ENGLISH, "%d is (negative) selected", w)))
              .setPositiveButton(android.R.string.ok, (d, w) -> {
                if (view.isAvailable()) {
                  view.requestPermissionsCompat(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, PERMISSION_READ_STORAGE);
                }
              })
              .create();
          dialog.show();
        } else {
          view.requestPermissionsCompat(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, PERMISSION_READ_STORAGE);
        }
      }
    }
  }

  private String toImageFileName() {
    return String.format(Locale.ENGLISH,
        "%s_%s", FILE_PREFIX, timeStamp.format(new Date()));
  }

  private String toEntityName(ImageEntity entity) {
    String path = entity.getImageUri().getPath();
    return path.substring(path.lastIndexOf("/"));
  }

  private void createIfImageListNotExists() {
    if (images == null) {
      images = new ArrayList<>();
    }
  }
}