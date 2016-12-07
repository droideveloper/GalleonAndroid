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

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import java.io.File;
import java.util.List;
import org.fs.common.AbstractPresenter;
import org.fs.galleon.BuildConfig;
import org.fs.galleon.R;
import org.fs.galleon.managers.IFileManager;
import org.fs.galleon.managers.IPrefManager;
import org.fs.galleon.usecases.IProvidePdfUseCase;
import org.fs.galleon.views.IPickUploadActivityView;
import org.fs.util.Collections;

public class PickUploadActivityPresenter extends AbstractPresenter<IPickUploadActivityView>
    implements IPickUploadActivityPresenter, IProvidePdfUseCase.Callback {


  //TODO use those items to dispatch upload or download but this is not gone be here because main purpose of this
  //TODO activity is to filter files of Tools directory to show user upload option in order to find proper concept of
  //TODO handle image->pdf conversion in GalleonAndroid Application.
  private final IFileManager fileManager;
  private final IPrefManager prefManager;

  public PickUploadActivityPresenter(IPickUploadActivityView view, IFileManager fileManager, IPrefManager prefManager) {
    super(view);
    this.fileManager = fileManager;
    this.prefManager = prefManager;
  }

  @Override public void onCreate() {
    view.setupViews();
    String titleStr = view.getResourceString(R.string.app_name);//TODO change this proper title
    view.setTitleText(titleStr);
  }

  @Override public void onStart() {
    //TODO put usecase that reads tools directory for pdf files.
  }

  @Override public Toolbar.OnClickListener provideCancelListener() {
    return (v) -> backPressed();
  }

  @Override public View.OnClickListener provideUploadListener() {
    return (v) -> {
      Intent data = new Intent();
      //TODO fill intent with file locations and picked results.
      view.setResult(Activity.RESULT_OK, data);
      view.finish();
    };
  }

  @Override public boolean onOptionsMenuItem(MenuItem item) {
    //TODO check for plus sign for adding files to current user.
    return false;
  }

  @Override public void backPressed() {
    view.setResult(Activity.RESULT_CANCELED);
    view.finish();
  }

  @Override public void onSuccess(List<File> dataSet) {
    if (!Collections.isNullOrEmpty(dataSet)) {
      view.setAdapter(dataSet);
      view.showFoundItems();
    } else {
      log(Log.WARN, "noting found as pdf");
      view.showNothingFound();
    }
  }

  @Override public void onError(Throwable thr) {
    log(thr);
    String errorStr = view.getResourceString(R.string.app_name);//TODO change proper error String
    view.showError(errorStr);//show user related error
    view.showNothingFound();//nothing found
  }

  @Override public void onCompleted() {
    log(Log.INFO, "pdf files usecase completed");
    if (view.isProgressVisible()) {
      view.hideProgress();//if progress visible hide it.
    }
  }

  @Override protected String getClassTag() {
    return PickUploadActivityPresenter.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }
}