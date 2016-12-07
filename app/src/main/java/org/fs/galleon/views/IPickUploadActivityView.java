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
package org.fs.galleon.views;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.view.View;
import java.io.File;
import java.util.List;
import org.fs.common.IView;

public interface IPickUploadActivityView extends IView {

  void setupViews();
  void setTitleText(String titleStr);
  void setAdapter(List<File> dataSet);

  void showError(String errorStr);
  void showError(String errorStr, String actionStr, View.OnClickListener callback);

  void showProgress();
  void hideProgress();

  void showNothingFound();//this will show user that we can not find anything
  void showFoundItems();//this will show user items that we found

  void setResult(int resultCode);
  void setResult(int resultCode, Intent data);
  void finish();

  boolean isProgressVisible();
  String getResourceString(@StringRes int res);
  Context getContext();
}