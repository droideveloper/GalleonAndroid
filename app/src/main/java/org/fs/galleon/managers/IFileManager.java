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

import java.io.File;
import java.util.List;
import java.util.zip.GZIPInputStream;
import rx.Observable;

public interface IFileManager {

  /**
   *
   * @return
   */
  File defaultDirectory();

  /**
   *
   * @return
   */
  File syncDirectory();

  /**
   *
   * @return
   */
  File toolsDirectory();

  List<File> searchFiles(File directory, SearchOptions options, String fileExtension);

  /**
   *
   * @param directory
   * @return
   */
  Observable<File> createIfNotExists(String directory);

  /**
   *
   * @param directory
   * @return
   */
  Observable<File> createIfNotExists(File directory);

  /**
   *
   * @param directory
   * @param append
   * @return
   */
  Observable<File> combinePath(String directory, String append);

  /**
   *
   * @param directory
   * @param append
   * @return
   */
  Observable<File> combinePath(File directory, String append);

  /**
   *
   * @param file
   * @return
   */
  Observable<File> toGzipFile(File file);

  /**
   *
   * @param input
   * @return
   */
  Observable<File> toCompressedFile(File input);

  /**
   *
   * @param file
   * @param input
   * @return
   */
  Observable<File> toDecompressedFile(File file, GZIPInputStream input);

  void start();
  void stop();

  enum SearchOptions {
    ALL_DIRECTORIES,
    ONLY_SELF
  }
}
