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
package org.pdf.haru;

import java.io.File;

public final class PdfCore {
  static {
    System.loadLibrary("jpgt");
    System.loadLibrary("pngt");
    System.loadLibrary("harupdf_java");
  }

  private PdfCore() {
    throw new IllegalArgumentException("you can not have instance of this object");
  }

  public static boolean createFromJpeg(File jfile, int width, int height, File pfile) {
    return createFromJpeg(jfile.getAbsolutePath(), width, height, pfile.getAbsolutePath());
  }

  private static native boolean createFromJpeg(String jpefFilename, int width, int height, String pdfFilename);
}
