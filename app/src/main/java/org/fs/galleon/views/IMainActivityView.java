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
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import org.fs.common.IView;
import org.fs.galleon.components.ApplicationComponent;

public interface IMainActivityView extends IView {

  void setUpViews();
  void setTitleText(String titleStr);

  void setSelected(@IdRes int id);
  <T extends Fragment> void replaceAndCommit(T view);
  boolean onOptionsItemSelected(MenuItem item);
  void startActivity(Intent intent);
  void startActivityForResult(Intent intent, int requestCode);
  void finish();
  boolean isMenuOpen();
  void closeMenu();
  void openMenu();

  boolean isSelected(@IdRes int id);

  ApplicationComponent getApplicationComponent();

  boolean isSearchVisible();
  void showSearch();
  void hideSearch();

  void showProgress();
  void hideProgress();
  boolean isProgressVisible();

  void showError(String errorStr);
  void showError(String errorStr, String btnText, View.OnClickListener listener);

  Context getContext();
}