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
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import javax.inject.Inject;
import org.fs.core.AbstractActivity;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.R;
import org.fs.galleon.components.ApplicationComponent;
import org.fs.galleon.components.DaggerActivityComponent;
import org.fs.galleon.modules.ActivityModule;
import org.fs.galleon.presenters.ILoginActivityPresenter;
import org.fs.util.ViewUtility;

public class LoginActivityView extends AbstractActivity<ILoginActivityPresenter>
    implements ILoginActivityView {

  private TextInputLayout txtLayoutUserName;
  private TextInputLayout txtLayoutPassword;
  private TextInputEditText txtEditUserName;
  private TextInputEditText txtEditPassword;
  private AppCompatCheckBox checkRememberMe;
  private AppCompatButton loginButton;

  private View viewContent;
  private View viewProgress;

  @Inject ILoginActivityPresenter presenter;

  @Override public void onCreate(Bundle restoreState) {
    super.onCreate(restoreState);
    setContentView(R.layout.view_login_activity);
    //dagger will inject it
    DaggerActivityComponent.builder()
        .applicationComponent(getApplicationComponent())
        .activityModule(new ActivityModule(this))
        .build()
        .inject(this);
    //if we call this activity with intent it will read it as that way or we read it via
    //previous state we stored
    presenter.restoreState(restoreState != null ? restoreState : getIntent().getExtras());
    presenter.onCreate();
  }

  @Override protected void onSaveInstanceState(Bundle storeState) {
    super.onSaveInstanceState(storeState);
    presenter.storeState(storeState);
  }

  @Override public void onStart() {
    super.onStart();
    presenter.observeUsernameChange(txtEditUserName);
    presenter.observePasswordChange(txtEditPassword);
    presenter.observeRememberChange(checkRememberMe);
    presenter.observeLoginClick(loginButton);
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

  @Override public void setTitleText(String titleStr) {
    setTitle(titleStr);
  }

  @Override public void setUpView() {
    txtLayoutUserName = ViewUtility.findViewById(this, R.id.txtLayoutUserName);
    txtEditUserName = ViewUtility.findViewById(this, R.id.txtEditUserName);
    txtLayoutPassword = ViewUtility.findViewById(this, R.id.txtLayoutPassword);
    txtEditPassword = ViewUtility.findViewById(this, R.id.txtEditPassword);

    checkRememberMe = ViewUtility.findViewById(this, R.id.checkRememberMe);
    loginButton = ViewUtility.findViewById(this, R.id.btnLogin);

    viewContent = ViewUtility.findViewById(this, R.id.viewContent);
    viewProgress = ViewUtility.findViewById(this, R.id.viewProgress);
  }

  @Override public void showUserNameError(String str) {
    txtLayoutUserName.setErrorEnabled(true);
    txtLayoutUserName.setError(str);
  }

  @Override public void hideUserNameError() {
    txtLayoutUserName.setError(null);
    txtLayoutUserName.setErrorEnabled(false);
  }

  @Override public void showPasswordError(String str) {
    txtLayoutPassword.setErrorEnabled(true);
    txtLayoutPassword.setError(str);
  }

  @Override public void hidePasswordError() {
    txtLayoutPassword.setError(null);
    txtLayoutPassword.setErrorEnabled(false);
  }

  @Override public void showError(String str) {
    Snackbar.make(view(), str, Snackbar.LENGTH_LONG)
        .show();
  }

  @Override public void showError(String str, String buttonName, View.OnClickListener listener) {
    final Snackbar snackbar = Snackbar.make(view(), str, Snackbar.LENGTH_LONG);
    snackbar.setAction(buttonName, (v) -> {
      if (listener != null) {
        listener.onClick(v);
      }
      snackbar.dismiss();
    });
    snackbar.show();
  }

  @Override public void showProgress() {
    viewContent.setVisibility(View.GONE);
    viewProgress.setVisibility(View.VISIBLE);
  }

  @Override public void hideProgress() {
    viewProgress.setVisibility(View.GONE);
    viewContent.setVisibility(View.VISIBLE);
  }

  @Override public void startActivity(Intent intent) {
    super.startActivity(intent);
    overridePendingTransition(R.anim.translate_in_right, R.anim.scale_out);
  }

  @Override public void finish() {
    super.finish();
    overridePendingTransition(R.anim.scale_in, R.anim.translate_out_right);
  }

  @Override public boolean isProgressVisible() {
    return viewProgress.getVisibility() == View.VISIBLE;
  }

  @Override public Context getContext() {
    return this;
  }

  @Override protected String getClassTag() {
    return LoginActivityView.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }

  private View view() {
    return findViewById(android.R.id.content);
  }

  private GalleonApplication galleonApplication() {
    return (GalleonApplication) getApplication();
  }

  private ApplicationComponent getApplicationComponent() {
    return galleonApplication().applicationComponent();
  }
}