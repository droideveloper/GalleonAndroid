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
import android.os.Parcelable;
import android.widget.Button;
import com.jakewharton.rxbinding.view.RxView;
import org.fs.common.AbstractPresenter;
import org.fs.common.BusManager;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.events.ApproveEvent;
import org.fs.galleon.views.IApproveDialogFragmentView;
import org.fs.util.StringUtility;
import rx.Subscription;

public class ApproveDialogFragmentPresenter extends AbstractPresenter<IApproveDialogFragmentView>
    implements IApproveDialogFragmentPresenter {

  public final static String KEY_MESSAGE_STR    = "dialog.fragment.message.str";
  public final static String KEY_ARG_PARCELABLE = "dialog.fragment.parcelable";

  private String messageStr;
  private Parcelable arguments;

  private Subscription okSubscription;
  private Subscription cancelSubscription;

  public ApproveDialogFragmentPresenter(IApproveDialogFragmentView view) {
    super(view);
  }

  @Override public void onStart() {
    if (!StringUtility.isNullOrEmpty(messageStr)) {
      if(view.isAvailable()) {
        view.setMessageText(messageStr);
      }
    }
  }

  @Override public void onStop() {
    if (okSubscription != null) {
      okSubscription.unsubscribe();
      okSubscription = null;
    }
    if (cancelSubscription != null) {
      cancelSubscription.unsubscribe();
      cancelSubscription = null;
    }
  }

  @Override protected String getClassTag() {
    return ApproveDialogFragmentPresenter.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }

  @Override public void restoreState(Bundle restoreState) {
    if (restoreState != null) {
      if (restoreState.containsKey(KEY_MESSAGE_STR)) {
        messageStr = restoreState.getString(KEY_MESSAGE_STR);
      }
      if(restoreState.containsKey(KEY_ARG_PARCELABLE)) {
        arguments = restoreState.getParcelable(KEY_ARG_PARCELABLE);
      }
    }
  }

  @Override public void storeState(Bundle storeState) {
    if (!StringUtility.isNullOrEmpty(messageStr)) {
      storeState.putString(KEY_MESSAGE_STR, messageStr);
    }
    if (!StringUtility.isNullOrEmpty(arguments)) {
      storeState.putParcelable(KEY_ARG_PARCELABLE, arguments);
    }
  }

  @Override public void observeOptions(Button buttonOk, Button buttonCancel) {
    okSubscription = RxView.clicks(buttonOk)
        .subscribe(ignored -> {
          if (view.isAvailable()) {
            BusManager.send(new ApproveEvent(true, arguments));
            view.dismiss();
          }
        });
    cancelSubscription = RxView.clicks(buttonCancel)
        .subscribe(ignored -> {
          if (view.isAvailable()) {
            BusManager.send(new ApproveEvent(false, null));
            view.dismiss();
          }
        });
  }
}