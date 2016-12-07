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
import java.util.List;
import org.fs.core.AbstractApplication;
import org.fs.galleon.entities.Document;
import org.fs.galleon.nets.IEndpoint;
import org.fs.galleon.nets.Response;
import org.fs.util.PreconditionUtility;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class CustomerDocumentsUseCase implements ICustomerDocumentsUseCase {

  private final int customerId;
  private final IEndpoint endpoint;
  private boolean isActive;
  private Subscription cancelable;

  private CustomerDocumentsUseCase(final int customerId, final IEndpoint endpoint) {
    this.customerId = customerId;
    this.endpoint = endpoint;
  }

  @Override public void executeAsync(Callback callback) {
    PreconditionUtility.checkNotNull(callback, "callback is null.");
    isActive = true;
    cancelable = asObservable().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(callback::onSuccess, callback::onError, () -> {
          isActive = false;
          callback.onCompleted();
        });
  }

  @Override public boolean isActive() {
    return isActive;
  }

  @Override public void cancelIfActive() {
    if (isActive()) {
      cancelable.unsubscribe();
      cancelable = null;
    }
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
    return CustomerDocumentsUseCase.class.getSimpleName();
  }

  private void checkIfParamsNotNull() {
    PreconditionUtility.throwIfConditionFails(customerId >= 0, "customerId can not be negative.");
    PreconditionUtility.checkNotNull(endpoint, "endpoint is null.");
  }

  @Override public Observable<Response<List<Document>>> asObservable() {
    checkIfParamsNotNull();
    return endpoint.queryDocumentsByDirectoryID(customerId);
  }

  //Builder class for this usecase
  public static class Builder {
    private int customerId;
    private IEndpoint endpoint;

    public Builder() { }
    public Builder customerId(int customerId) { this.customerId = customerId; return this; }
    public Builder endpoint(IEndpoint endpoint) { this.endpoint = endpoint; return this; }
    public CustomerDocumentsUseCase build() {
      return new CustomerDocumentsUseCase(customerId, endpoint);
    }
  }
}