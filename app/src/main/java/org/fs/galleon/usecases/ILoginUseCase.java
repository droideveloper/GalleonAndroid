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

import org.fs.common.IUseCase;
import org.fs.galleon.entities.Session;
import org.fs.galleon.nets.Response;

public interface ILoginUseCase extends IUseCase<Response<Session>> {

  /**
   * Async call of task
   * @param callback callback instance for end of execution to notify caller
   */
  void executeAsync(final Callback callback);

  /**
   * Callback interface that returns result or error
   */
  interface Callback {
    /**
     * Success callback method of restRequest
     * @param response response of request if success
     */
    void onSuccess(Response<Session> response);

    /**
     * Error callback of restRequest
     * @param thr throwable instance that holds what's gone wrong
     */
    void onError(Throwable thr);

    /**
     * Completed callback
     */
    void onCompleted();
  }
}