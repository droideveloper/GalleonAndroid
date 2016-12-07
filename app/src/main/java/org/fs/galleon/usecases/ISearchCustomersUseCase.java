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

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.fs.common.IUseCase;
import org.fs.galleon.entities.Customer;
import org.fs.galleon.nets.Response;

public interface ISearchCustomersUseCase extends IUseCase<Response<List<Customer>>> {

  void executeAsync(final Callback callback);
  void executeAsyncWidthDelay(final Callback callback, long delay, TimeUnit unit);
  SearchCustomersUseCase.Builder newBuilder();

  interface Callback {
    void onSuccess(Response<List<Customer>> response);
    void onError(Throwable thr);
    void onCompleted();
  }
}