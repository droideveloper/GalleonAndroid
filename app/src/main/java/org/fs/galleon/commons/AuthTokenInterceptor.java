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
package org.fs.galleon.commons;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.fs.galleon.managers.IPrefManager;
import org.fs.galleon.managers.PrefManager;
import org.fs.util.StringUtility;

public final class AuthTokenInterceptor implements Interceptor {

  private final static String KEY_AUTH_TOKEN = "X-Auth-Token";

  private final IPrefManager prefManager;

  public AuthTokenInterceptor(IPrefManager prefManager) {
    this.prefManager = prefManager;
  }

  @Override public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    String token = prefManager.getValue(PrefManager.KEY_AUTH_TOKEN, StringUtility.EMPTY);
    if(!StringUtility.isNullOrEmpty(token)) {
      request = request.newBuilder()
                       .addHeader(KEY_AUTH_TOKEN, token)
                       .build();
    }
    return chain.proceed(request);
  }
}
