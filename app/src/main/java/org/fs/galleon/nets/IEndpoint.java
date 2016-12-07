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

import java.util.List;
import org.fs.galleon.entities.Category;
import org.fs.galleon.entities.City;
import org.fs.galleon.entities.Contact;
import org.fs.galleon.entities.Country;
import org.fs.galleon.entities.Credential;
import org.fs.galleon.entities.Customer;
import org.fs.galleon.entities.Directory;
import org.fs.galleon.entities.Document;
import org.fs.galleon.entities.Session;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface IEndpoint {

  @POST(value = "/v1/endpoint/sign-in")
  Observable<Response<Session>> tryLogin(@Body Credential credential);

  @Headers(value = "Content-Type: application/json; charset=utf-8")
  @POST(value = "/v1/endpoint/keep-alive")
  Observable<Response<Session>> tryKeepAlive(@Body String token);

  @POST(value = "/v1/endpoint/customers-query/{query}")
  Observable<Response<List<Customer>>> queryCustomers(@Path(value = "query") String query);

  @POST(value = "/v1/endpoint/create/customer")
  Observable<Response<Customer>> createCustomer(@Body Customer customer);

  @POST(value = "/v1/endpoint/create/contact")
  Observable<Response<Contact>> createContact(@Body Contact contact);

  @POST(value = "/v1/endpoint/create/contacts")
  Observable<Response<List<Contact>>> createContacts(@Body List<Contact> contacts);

  @POST(value = "/v1/endpoint/create/documents")
  Observable<Response<List<Document>>> createDocuments(@Body List<Document> documents);

  @POST(value = "/v1/endpoint/create/directory")
  Observable<Response<Directory>> createDirectory(@Body Directory directory);

  @POST(value = "/v1/endpoint/update/document")
  Observable<Response<Document>> updateDocument(@Body Document document);

  @GET(value = "/v1/endpoint/directory/{directoryId}")
  Observable<Response<Directory>> queryDirectory(@Path(value = "directoryId") int directoryId);

  @GET(value = "/v1/endpoint/document/{documentId}")
  Observable<Response<Document>> queryDocument(@Path(value = "documentId") int documentId);

  @GET(value = "/v1/endpoint/documents/{directoryId}")
  Observable<Response<List<Document>>> queryDocumentsByDirectoryID(@Path(value = "directoryId") int directoryId);

  @GET(value = "/v1/endpoint/countries")
  Observable<Response<List<Country>>> queryCountries();

  @GET(value = "/v1/endpoint/cities/{countryId}")
  Observable<Response<List<City>>> queryCities(@Path(value = "countryId") int countryId);

  @GET(value = "/v1/endpoint/categories")
  Observable<Response<List<Category>>> queryCategories();
}
