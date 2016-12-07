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
package org.fs.galleon.usecases;

import android.util.Log;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.entities.Credential;
import org.fs.galleon.entities.Session;
import org.fs.galleon.nets.IEndpoint;
import org.fs.galleon.nets.Response;
import org.fs.util.PreconditionUtility;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class LoginUseCase implements ILoginUseCase {

  private final Credential credential;
  private final IEndpoint  endpoint;

  private LoginUseCase(Credential credential, IEndpoint endpoint) {
    this.credential = credential;
    this.endpoint = endpoint;
  }

  @Override public void executeAsync(Callback callback) {
    PreconditionUtility.checkNotNull(callback, "callback can not be null.");
    asObservable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(callback::onSuccess, callback::onError, callback::onCompleted);
  }

  @Override public Observable<Response<Session>> asObservable() {
    checkIfParamsNotNull();
    return endpoint.tryLogin(credential);
  }

  private void checkIfParamsNotNull() {
    PreconditionUtility.checkNotNull(credential, "credential is null");
    PreconditionUtility.checkNotNull(endpoint, "endpoint is null");
  }

  protected void log(String msg) {
    log(Log.DEBUG, msg);
  }

  protected void log(Throwable exp) {
    StringWriter strWriter = new StringWriter(128);
    PrintWriter ptrWriter = new PrintWriter(strWriter);
    exp.printStackTrace(ptrWriter);
    log(Log.ERROR, strWriter.toString());
  }

  private void log(int lv, String msg) {
    if (isLogEnabled()) {
      Log.println(lv, getClassTag(), msg);
    }
  }

  protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }

  protected String getClassTag() {
    return LoginUseCase.class.getSimpleName();
  }

  /**
   * Builder class for LoginUseCase instance creation
   */
  public static class Builder {
    private Credential credential;
    private IEndpoint endpoint;

    public Builder() {}
    public Builder credential(Credential credential) { this.credential = credential; return this; }
    public Builder endpoint(IEndpoint endpoint) { this.endpoint = endpoint; return this; }
    public LoginUseCase build() {
      return new LoginUseCase(credential, endpoint);
    }
  }
}