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
import android.net.Uri;
import android.view.View;
import java.util.List;
import org.fs.common.IView;
import org.fs.galleon.entities.ImageEntity;

import static android.view.View.OnClickListener;

public interface IToolsFragmentView extends IView {

  View setUpViews(View view);

  ImageEntity removeAt(int position);
  void addAt(int position, ImageEntity entity);
  void add(ImageEntity entity);
  void bindRecyclerAdapter(List<ImageEntity> dataSet);
  void setFileTitle(String fileName);
  void setPdfFile(Uri uri);
  void unloadPdfFile();
  void clearAllAdapters();

  void showError(String errorStr);
  void showError(String errorStr, String buttonText, OnClickListener listener);

  void showProgress();
  void hideProgress();

  void startActivityForResult(Intent intent, int requestCode);

  boolean shouldShowRequestPermissionRationale(String permission);
  void requestPermissionsCompat(String[] permissions, int requestCode);

  boolean isAvailable();
  Context getContext();
}