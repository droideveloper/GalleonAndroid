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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxTextView;
import java.util.Locale;
import org.fs.common.AbstractPresenter;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.R;
import org.fs.galleon.commons.PasswordValidator;
import org.fs.galleon.commons.UserNameValidator;
import org.fs.galleon.entities.Credential;
import org.fs.galleon.entities.Session;
import org.fs.galleon.managers.IPrefManager;
import org.fs.galleon.managers.PrefManager;
import org.fs.galleon.nets.IEndpoint;
import org.fs.galleon.nets.Response;
import org.fs.galleon.usecases.ILoginUseCase;
import org.fs.galleon.usecases.LoginUseCase;
import org.fs.galleon.views.ILoginActivityView;
import org.fs.galleon.views.MainActivityView;
import org.fs.util.StringUtility;
import rx.Subscription;

public class LoginActivityPresenter extends AbstractPresenter<ILoginActivityView>
    implements ILoginActivityPresenter {

  private final static String KEY_USERNAME = "login.activity.username";
  private final static String KEY_PASSWORD = "login.activity.password";

  private Subscription usernameSub;
  private Subscription passwordSub;
  private Subscription rememberMeSub;
  private Subscription buttonSub;

  private String username, password;

  private IPrefManager prefManager;
  private IEndpoint endpoint;
  private ILoginUseCase usecase;

  public LoginActivityPresenter(ILoginActivityView view, IPrefManager prefManager, IEndpoint endpoint) {
    super(view);
    this.endpoint = endpoint;
    this.prefManager = prefManager;
  }

  @Override public void restoreState(Bundle restoreState) {
    if(restoreState != null) {
      if (restoreState.containsKey(KEY_USERNAME)) {
        username = restoreState.getString(KEY_USERNAME);
      }
      if (restoreState.containsKey(KEY_PASSWORD)) {
        password = restoreState.getString(KEY_PASSWORD);
      }
    }
  }

  @Override public void storeState(Bundle storeState) {
    if (!StringUtility.isNullOrEmpty(username)) {
      storeState.putString(KEY_USERNAME, username);
    }
    if (!StringUtility.isNullOrEmpty(password)) {
      storeState.putString(KEY_PASSWORD, password);
    }
  }

  @Override public void onCreate() {
    String titleStr = view.getContext().getString(R.string.titleLoginActivityView);
    view.setTitleText(titleStr);
    view.setUpView();
  }

  @Override public void onStart() {
    boolean rememberMe = prefManager.getValue(PrefManager.KEY_REMEMBER_ME, false);
    if (rememberMe) {
      String userName = prefManager.getValue(PrefManager.KEY_USERNAME, StringUtility.EMPTY);
      String password = prefManager.getValue(PrefManager.KEY_PASSWORD, StringUtility.EMPTY);
      if(!StringUtility.isNullOrEmpty(userName) && !StringUtility.isNullOrEmpty(password)) {
        view.showProgress();
        //build usecase
        usecase = new LoginUseCase.Builder()
            .endpoint(endpoint)
            .credential(toCredential(userName, password))
            .build();
        //execute task
        usecase.executeAsync(new ILoginUseCase.Callback() {
          @Override public void onSuccess(Response<Session> response) {
            if (response.isSuccess()) {
              prefManager.setValue(PrefManager.KEY_AUTH_TOKEN, response.data().getToken());
              view.startActivity(new Intent(view.getContext(), MainActivityView.class));
              view.finish();
            } else {
              log(Log.WARN,
                  String.format(Locale.ENGLISH, "ErrorCode: %d\nErrorMessage: %s\n", response.code(), response.message()));
              String error = view.getContext().getString(R.string.strErrorUsernameOrPasswordInvalid);
              view.showError(error);
            }
          }

          @Override public void onError(Throwable thr) {
            log(thr);
          }

          @Override public void onCompleted() {
            view.hideProgress();
          }
        });
      }
    }
  }

  @Override public void onStop() {
    if (!usernameSub.isUnsubscribed()) {
      usernameSub.unsubscribe();
    }
    if (!passwordSub.isUnsubscribed()) {
      passwordSub.unsubscribe();
    }
    if (!rememberMeSub.isUnsubscribed()) {
      rememberMeSub.unsubscribe();
    }
    if (!buttonSub.isUnsubscribed()) {
      buttonSub.unsubscribe();
    }
  }

  @Override public void observeUsernameChange(TextView usernameTextView) {
    usernameSub = RxTextView.textChanges(usernameTextView)
                    .map(CharSequence::toString)
                    .map(str -> {
                      UserNameValidator validator = new UserNameValidator();
                      return validator.validate(str, Locale.getDefault());
                    })
                    .subscribe(validation -> {
                      if (!validation.isValid()) {
                        String errorUserName = view.getContext().getString(R.string.strErrorUserName);
                        view.showUserNameError(errorUserName);
                        username = validation.value();
                      } else {
                        view.hideUserNameError();
                        this.username = validation.value();
                      }
                    });
  }

  @Override public void observePasswordChange(TextView passwordTextView) {
    passwordSub = RxTextView.textChanges(passwordTextView)
                    .map(CharSequence::toString)
                    .map(str -> {
                      //todo validator gets it here as hashed value so you do not need to hash again!
                      PasswordValidator validator = new PasswordValidator();
                      return validator.validate(str, Locale.getDefault());
                    })
                    .subscribe(validation -> {
                      if(!validation.isValid()) {
                        String errorPassword = view.getContext().getString(R.string.strErrorPassword);
                        view.showPasswordError(errorPassword);
                        this.password = validation.value(); //here is null
                      } else {
                        view.hidePasswordError();
                        this.password = validation.value();
                      }
                    });
  }

  @Override public void observeRememberChange(CheckBox rememberMeCheckbox) {
    //if we do not read it in here it always sets it false god damn Jake..
    boolean isChecked = prefManager.getValue(PrefManager.KEY_REMEMBER_ME, false);
    rememberMeSub = RxCompoundButton.checkedChanges(rememberMeCheckbox)
                      .subscribe(checked -> {
                        prefManager.setValue(PrefManager.KEY_REMEMBER_ME, checked);
                      });
    rememberMeCheckbox.setChecked(isChecked);
  }

  @Override public void observeLoginClick(Button loginButton) {
    buttonSub = RxView.clicks(loginButton)
                  .map(x -> !StringUtility.isNullOrEmpty(username)
                      && !StringUtility.isNullOrEmpty(password))
                  .subscribe(isValid -> {
                      if (isValid) {
                        view.showProgress();
                        //build usecase
                        usecase = new LoginUseCase.Builder()
                            .endpoint(endpoint)
                            .credential(toCredential())
                            .build();
                        //execute task
                        usecase.executeAsync(new ILoginUseCase.Callback() {
                          @Override public void onSuccess(Response<Session> response) {
                            if (response.isSuccess()) {
                              boolean isRememberMe = prefManager.getValue(PrefManager.KEY_REMEMBER_ME, false);
                              if (isRememberMe) {
                                prefManager.setValue(PrefManager.KEY_USERNAME, username);
                                prefManager.setValue(PrefManager.KEY_PASSWORD, password);
                                prefManager.setValue(PrefManager.KEY_AUTH_TOKEN, response.data().getToken());
                              } else {
                                prefManager.setValue(PrefManager.KEY_AUTH_TOKEN, response.data().getToken());
                              }
                              view.startActivity(new Intent(view.getContext(), MainActivityView.class));
                              view.finish();
                            } else {
                              log(Log.WARN,
                                  String.format(Locale.ENGLISH, "ErrorCode: %d\nErrorMessage: %s\n", response.code(), response.message()));
                              String error = view.getContext().getString(R.string.strErrorUsernameOrPasswordInvalid);
                              view.showError(error);
                            }
                          }

                          @Override public void onError(Throwable thr) {
                            log(thr);
                          }

                          @Override public void onCompleted() {
                            view.hideProgress();
                          }
                        });
                      } else {
                        String error = view.getContext().getString(R.string.strErrorUsernameOrPasswordInvalid);
                        view.showError(error);
                      }
                  });
  }

  @Override protected String getClassTag() {
    return LoginActivityPresenter.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }

  @Override protected void log(Throwable error) {
    super.log(error);
    if (view.isProgressVisible()) {
      view.hideProgress();
    }
  }

  private Credential toCredential(String userName, String password) {
    return new Credential.Builder()
        .password(password)
        .userName(userName)
        .build();
  }

  private Credential toCredential() {
    return new Credential.Builder()
        .password(password)
        .userName(username)
        .build();
  }
}