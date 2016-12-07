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
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.fs.core.AbstractApplication;
import org.fs.galleon.entities.Session;
import org.fs.galleon.managers.IPrefManager;
import org.fs.galleon.managers.PrefManager;
import org.fs.galleon.nets.IEndpoint;
import org.fs.galleon.nets.Response;
import org.fs.util.PreconditionUtility;
import org.fs.util.StringUtility;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class KeepAliveUseCase implements IKeepAliveUseCase, IKeepAliveUseCase.Callback {

  private final IPrefManager prefManager;
  private final IEndpoint    endpoint;

  private final static long     KEEP_ALIVE_INTERVAL = 25L;
  private final static TimeUnit KEEP_ALIVE_UNITS    = TimeUnit.MINUTES;

  private Subscription elapsedEventListener;

  private KeepAliveUseCase(final IPrefManager prefManager, IEndpoint endpoint) {
    this.prefManager = prefManager;
    this.endpoint = endpoint;
  }

  @Override public void start() {
    checkIfParamsNotNull();
    if (!checkIfAlreadyExecuted()) {
      elapsedEventListener = Observable.interval(KEEP_ALIVE_INTERVAL, KEEP_ALIVE_UNITS)
          .flatMap(x -> asObservable())
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(this::onSuccess, this::onError, this::onCompleted);
    }
  }

  @Override public void stop() {
    if (checkIfAlreadyExecuted()) {
      elapsedEventListener.unsubscribe();
      elapsedEventListener = null;
    }
  }

  @Override public void onSuccess(Response<Session> response) {
    if (response.isSuccess()) {
      Session session = response.data();
      if (!StringUtility.isNullOrEmpty(session.getToken())) {
        prefManager.setValue(PrefManager.KEY_AUTH_TOKEN, session.getToken());
      }
    }
  }

  @Override public void onError(Throwable thr) {
    log(thr);
  }

  @Override public void onCompleted() {
    log(Log.INFO,
        String.format(Locale.ENGLISH, "%s completed.", toString())
    );
  }

  @Override public Observable<Response<Session>> asObservable() {
    final String token = prefManager.getValue(PrefManager.KEY_AUTH_TOKEN, StringUtility.EMPTY);
    return endpoint.tryKeepAlive(token);
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

  protected void log(int lv, String msg) {
    if (isLogEnabled()) {
      Log.println(lv, getClassTag(), msg);
    }
  }

  protected boolean isLogEnabled() {
    return AbstractApplication.isDebug();
  }

  protected String getClassTag() {
    return KeepAliveUseCase.class.getSimpleName();
  }

  private void checkIfParamsNotNull() {
    PreconditionUtility.checkNotNull(endpoint, "endpoint is null");
    PreconditionUtility.checkNotNull(prefManager, "prefManager is null");
  }

  private boolean checkIfAlreadyExecuted() {
    return elapsedEventListener != null && elapsedEventListener.isUnsubscribed();
  }

  public static class Builder {
    private IPrefManager prefManager;
    private IEndpoint endpoint;

    public Builder() { }
    public Builder endpoint(IEndpoint endpoint) { this.endpoint = endpoint; return this; }
    public Builder prefManager(IPrefManager prefManager) { this.prefManager = prefManager; return this; }
    public KeepAliveUseCase build() {
      return new KeepAliveUseCase(prefManager, endpoint);
    }
  }
}