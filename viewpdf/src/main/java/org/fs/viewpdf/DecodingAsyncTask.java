/**
 * Copyright 2016 Bartosz Schiller
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fs.viewpdf;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import org.fs.viewpdf.util.FileUtils;

class DecodingAsyncTask extends AsyncTask<Void, Void, Throwable> {

  private WeakReference<PDFView> viewCache;

  private boolean cancelled;
  private String path;
  private boolean isAsset;

  private PdfiumCore pdfiumCore;
  private PdfDocument pdfDocument;
  private String password;

  public DecodingAsyncTask(String path, boolean isAsset, String password, PDFView pdfView,
      PdfiumCore pdfiumCore) {
    this.cancelled = false;
    this.isAsset = isAsset;
    this.password = password;
    this.pdfiumCore = pdfiumCore;
    this.path = path;
    this.viewCache = pdfView != null ? new WeakReference<>(pdfView) : null;
  }

  @Override protected Throwable doInBackground(Void... params) {
    try {
      if (isAsset) {
        final PDFView cached = getCachedView();
        if (cached != null) {
          cached.post(new Runnable() {
            @Override public void run() {
              try {
                path = FileUtils.fileFromAsset(cached.getContext(), path).getAbsolutePath();
              } catch (Throwable t) {
                throw new RuntimeException(t);
              }
            }
          });
        }
      }
      pdfDocument = pdfiumCore.newDocument(getSeekableFileDescriptor(path), password);
      return null;
    } catch (Throwable t) {
      return t;
    }
  }

  protected ParcelFileDescriptor getSeekableFileDescriptor(String path) throws IOException {
    ParcelFileDescriptor pfd = null;

    File pdfCopy = new File(path);
    if (pdfCopy.exists()) {
      pfd = ParcelFileDescriptor.open(pdfCopy, ParcelFileDescriptor.MODE_READ_ONLY);
      return pfd;
    }

    if (!path.contains("://")) {
      path = String.format("file://%s", path);
    }

    Uri uri = Uri.parse(path);
    final PDFView cached = getCachedView();
    if (cached != null) {
      Context context = cached.getContext();
      pfd = context.getContentResolver().openFileDescriptor(uri, "r");
    }

    if (pfd == null) {
      throw new IOException("Cannot get FileDescriptor for " + path);
    }

    return pfd;
  }

  @Override protected void onPostExecute(Throwable t) {
    final PDFView cached = getCachedView();
    if (cached != null) {
      if (t != null) {
        cached.loadError(t);
        return;
      }
      if (!cancelled) {
        cached.loadComplete(pdfDocument);
      }
    }
  }

  @Override protected void onCancelled() {
    cancelled = true;
  }

  private PDFView getCachedView() {
    return viewCache != null ? viewCache.get() : null;
  }
}
