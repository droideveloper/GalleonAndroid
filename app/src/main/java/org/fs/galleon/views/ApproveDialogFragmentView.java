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

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import javax.inject.Inject;
import org.fs.core.AbstractDialogFragment;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.R;
import org.fs.galleon.components.ApplicationComponent;
import org.fs.galleon.components.DaggerFragmentComponent;
import org.fs.galleon.modules.FragmentModule;
import org.fs.galleon.presenters.ApproveDialogFragmentPresenter;
import org.fs.galleon.presenters.IApproveDialogFragmentPresenter;
import org.fs.util.PreconditionUtility;
import org.fs.util.ViewUtility;

public class ApproveDialogFragmentView extends AbstractDialogFragment<IApproveDialogFragmentPresenter>
    implements IApproveDialogFragmentView {

  private TextView  txtMessage;
  private Button    buttonOk;
  private Button    buttonCancel;

  @Inject IApproveDialogFragmentPresenter presenter;

  public static ApproveDialogFragmentView newInstance(String messageStr, Parcelable arguments) {
    PreconditionUtility.checkNotNull(messageStr, "message can not be null.");
    ApproveDialogFragmentView fragment = new ApproveDialogFragmentView();
    Bundle args = new Bundle();
    if (arguments != null) {
      args.putParcelable(ApproveDialogFragmentPresenter.KEY_ARG_PARCELABLE, arguments);
    }
    args.putString(ApproveDialogFragmentPresenter.KEY_MESSAGE_STR, messageStr);
    fragment.setArguments(args);
    return fragment;
  }

  public ApproveDialogFragmentView() {
    super();
    setStyle(STYLE_NO_TITLE, getTheme());
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater factory, ViewGroup parent, Bundle restoreState) {
    return setUpViews(factory.inflate(R.layout.view_approve, parent, false));
  }

  @Override public void onActivityCreated(Bundle restoreState) {
    super.onActivityCreated(restoreState);
    //inject
    DaggerFragmentComponent.builder()
        .applicationComponent(getApplicationComponent())
        .fragmentModule(new FragmentModule(this))
        .build()
        .inject(this);
    presenter.restoreState(restoreState != null ? restoreState : getArguments());
    presenter.onCreate();
  }

  @Override public void onStart() {
    super.onStart();
    presenter.observeOptions(buttonOk, buttonCancel);
    presenter.onStart();
  }

  @Override public void onStop() {
    presenter.onStop();
    super.onStop();
  }

  @Override public void onDestroy() {
    presenter.onDestroy();
    super.onDestroy();
  }

  @Override protected String getClassTag() {
    return ApproveDialogFragmentView.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }

  @Override public View setUpViews(View view) {
    txtMessage = ViewUtility.findViewById(view, R.id.txtMessage);
    buttonOk = ViewUtility.findViewById(view, R.id.viewButtonOk);
    buttonCancel = ViewUtility.findViewById(view, R.id.viewButtonCancel);
    return view;
  }

  @Override public void setMessageText(String messageStr) {
    txtMessage.setText(messageStr);
  }

  @Override public boolean isAvailable() {
    return isCallingSafe();
  }

  private ApplicationComponent getApplicationComponent() {
    return ((GalleonApplication) getActivity().getApplication())
        .applicationComponent();
  }
}