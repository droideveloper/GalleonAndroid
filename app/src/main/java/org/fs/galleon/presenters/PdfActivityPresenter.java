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

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import java.io.File;
import org.fs.common.AbstractPresenter;
import org.fs.core.AbstractApplication;
import org.fs.galleon.views.IPdfActivityView;

public class PdfActivityPresenter extends AbstractPresenter<IPdfActivityView>
    implements IPdfActivityPresenter {

  private final static String BUNDLE_SRC_FILE_PATH = "bundle.src.file.path";

  private File pdfFile;

  public PdfActivityPresenter(IPdfActivityView view) {
    super(view);
  }

  @Override public void onCreate() {
    view.setup();
  }

  @Override public void onStart() {
    String filename = pdfFile.getName();
    view.setPdfTitle(filename);
    view.setPdfUri(Uri.fromFile(pdfFile));
  }

  @Override public void onDestroy() {
    view.recycle();
  }

  @Override public void backPressed() {
    finishView();
  }

  @Override public void restoreState(Bundle restoreState) {
     if (restoreState != null) {
       String filePath = restoreState.getString(BUNDLE_SRC_FILE_PATH);
       if (filePath != null) {
         pdfFile = new File(filePath);
       }
     }
  }

  @Override public void storeState(Bundle storeState) {
    if (pdfFile != null) {
      storeState.putString(BUNDLE_SRC_FILE_PATH, pdfFile.getAbsolutePath());
    }
  }

  @Override public Toolbar.OnClickListener provideOnClickListener() {
    return (v) -> finishView();
  }

  @Override protected String getClassTag() {
    return PdfActivityPresenter.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return AbstractApplication.isDebug();
  }

  private void finishView() {
    view.finish();
  }
}