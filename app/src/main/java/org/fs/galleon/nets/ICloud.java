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
package org.fs.galleon.nets;

import java.io.File;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import rx.Observable;

public interface ICloud {

  @Streaming
  @GET(value = "/v1/files/download-document")
  Observable<ResponseBody> downloadContent(@Header(value = "DocumentID") int documentId);

  @POST(value = "/v1/files/create-document")
  Observable<Response<Boolean>> createContent(@Header(value = "DocumentID") int documentId, @Body File file);

  @POST(value = "/v1/files/update-document")
  Observable<Response<Boolean>> updateContent(@Header(value = "DocumentID") int documentId, @Body File file);

  @POST(value = "/v1/files/delete-document")
  Observable<Response<Boolean>> deleteContent(@Header(value = "DocumentID") int documentId);
}
