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
import android.support.v4.app.FragmentManager;
import java.util.List;
import org.fs.common.IView;
import org.fs.galleon.entities.Contact;
import org.fs.galleon.entities.Document;

import static android.view.View.OnClickListener;

public interface ICustomerDetailActivityView extends IView {

  void showError(String str);
  void showError(String str, String buttonText, OnClickListener listener);

  void setUpViews();
  void setTitleText(String titleStr);
  void setSubTitleText(String subTitleStr);

  void bindDocumentAdapter(List<Document> dataSet);//will bind on recycler
  void bindContactAdapter(List<Contact> dataSet);//will bind on pager

  void removeDocumentAt(int position);
  FragmentManager getSupportFragmentManager();

  void finish();

  void showProgress();
  void hideProgress();

  void showProgressDelete();
  void hideProgressDelete();

  Context getContext();
}