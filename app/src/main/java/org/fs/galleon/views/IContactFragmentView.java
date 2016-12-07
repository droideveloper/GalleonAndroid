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
import android.support.v4.app.FragmentManager;
import android.view.View;
import org.fs.common.IView;

import static android.view.View.OnClickListener;

public interface IContactFragmentView extends IView {

  void setPhone(String phoneStr);
  void setAddress(String addressStr);
  void setCityAndCountry(String cityAndCountryStr);
  void hideCityAndCountry();
  void showCityAndCountry();

  View setUpView(View view);
  void startActivity(Intent intent);

  void showError(String errorStr);
  void showError(String errorStr, String btnStr, OnClickListener listener);

  boolean shouldShowRequestPermissionRationale(String permission);
  void requestPermissionsCompat(String[] permissions, int requestCode);

  FragmentManager getSupportFragmentManager();

  boolean isAvailable();
  Context getContext();
}