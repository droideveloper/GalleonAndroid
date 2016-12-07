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

import android.os.Bundle;
import java.io.File;
import org.fs.common.AbstractPresenter;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.views.IPreviewProcessFragmentView;
import org.fs.util.StringUtility;

public class PreviewProcessFragmentPresenter extends AbstractPresenter<IPreviewProcessFragmentView>
    implements IPreviewProcessFragmentPresenter {

  public final static String KEY_FILE_PATH = "pdf.file.path";


  private File file;

  public PreviewProcessFragmentPresenter(IPreviewProcessFragmentView view) {
    super(view);
  }

  @Override public void onStart() {
    if(view.isAvailable()) {
      view.setPdfFile(file);
    }
  }

  @Override public void restoreState(Bundle restoreState) {
    if (restoreState != null) {
      if (restoreState.containsKey(KEY_FILE_PATH)) {
        String path = restoreState.getString(KEY_FILE_PATH);
        if (!StringUtility.isNullOrEmpty(path)) {
          file = new File(path);
        }
      }
    }
  }

  @Override public void storeState(Bundle storeState) {
    if (file != null) {
      storeState.putString(KEY_FILE_PATH, file.getAbsolutePath());
    }
  }

  @Override protected String getClassTag() {
    return PreviewProcessFragmentPresenter.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }
}