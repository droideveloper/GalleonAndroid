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
import java.util.concurrent.TimeUnit;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.entities.Customer;
import org.fs.galleon.nets.IEndpoint;
import org.fs.galleon.nets.Response;
import org.fs.util.PreconditionUtility;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchCustomersUseCase implements ISearchCustomersUseCase {

  private final String query;
  private final IEndpoint endpoint;

  private Subscription delayed;

  private SearchCustomersUseCase(final String query, final IEndpoint endpoint, Subscription delayed) {
    this.query = query;
    this.endpoint = endpoint;
    this.delayed = delayed;
  }

  @Override public void executeAsyncWidthDelay(Callback callback, long delay, TimeUnit unit) {
    PreconditionUtility.checkNotNull(callback, "callback is null.");
    checkIfParamsNotNull();
    if (delayed != null && !delayed.isUnsubscribed()) {
      delayed.unsubscribe();
      delayed = null;
    }
    delayed = Observable.just(query)
        .throttleWithTimeout(delay, unit)
        .flatMap(endpoint::queryCustomers)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(callback::onSuccess, callback::onError, callback::onCompleted);
  }

  @Override public void executeAsync(Callback callback) {
    PreconditionUtility.checkNotNull(callback, "callback is null.");
    asObservable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(callback::onSuccess, callback::onError, callback::onCompleted);
  }

  @Override public Observable<Response<List<Customer>>> asObservable() {
    checkIfParamsNotNull();
    return endpoint.queryCustomers(query);
  }

  @Override public Builder newBuilder() {
    return new Builder()
        .delayed(delayed)
        .endpoint(endpoint)
        .query(query);
  }

  private void checkIfParamsNotNull() {
    PreconditionUtility.checkNotNull(query, "query is null or empty.");
    PreconditionUtility.checkNotNull(endpoint, "endpoint is null.");
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
    return GalleonApplication.isDebug();
  }

  protected String getClassTag() {
    return SearchCustomersUseCase.class.getSimpleName();
  }

  /**
   * Builder class
   */
  public static class Builder {
    private String query;
    private IEndpoint endpoint;
    private Subscription delayed;

    public Builder() {}
    public Builder query(String query) { this.query = query; return this; }
    public Builder endpoint(IEndpoint endpoint) { this.endpoint = endpoint; return this; }
    public Builder delayed(Subscription delayed) { this.delayed = delayed; return this; }
    public SearchCustomersUseCase build() {
      return new SearchCustomersUseCase(query, endpoint, delayed);
    }
  }
}