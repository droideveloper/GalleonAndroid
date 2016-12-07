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
package org.fs.galleon.modules;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import java.util.Date;
import javax.inject.Singleton;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.fs.galleon.commons.AuthTokenInterceptor;
import org.fs.galleon.commons.CSharpDateDeserializer;
import org.fs.galleon.commons.CSharpDateSerializer;
import org.fs.galleon.managers.DatabaseManager;
import org.fs.galleon.managers.FileManager;
import org.fs.galleon.managers.IDatabaseManager;
import org.fs.galleon.managers.IFileManager;
import org.fs.galleon.managers.IPrefManager;
import org.fs.galleon.managers.PrefManager;
import org.fs.galleon.nets.ICloud;
import org.fs.galleon.nets.IEndpoint;
import org.fs.net.RxJavaCallAdapterFactory;
import org.fs.net.converter.GsonConverterFactory;
import retrofit2.Retrofit;

@Module
public class ApplicationModule {

  private final Context context;
  private final String  baseEndpointURL;
  private final String  baseCloudURL;

  public ApplicationModule(Context context, String baseEndpointURL, String baseCloudURL) {
    this.context = context;
    this.baseEndpointURL = baseEndpointURL;
    this.baseCloudURL = baseCloudURL;
  }

  @Provides @Singleton IPrefManager providePrefManager() {
    return new PrefManager(defaultSharedPreferences(context));
  }

  @Provides @Singleton IDatabaseManager provideDatabaseManager() {
    return new DatabaseManager(context);
  }

  @Provides @Singleton OkHttpClient provideHttpClient(IPrefManager prefManager) {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    builder.addInterceptor(new AuthTokenInterceptor(prefManager));
    HttpLoggingInterceptor logger = new HttpLoggingInterceptor();//remove
    logger.setLevel(HttpLoggingInterceptor.Level.BODY);//remove
    builder.addInterceptor(logger);//todo remove this on production release
    return builder.build();
  }

  @Provides @Singleton Gson provideGson() {
    return new GsonBuilder()
                .registerTypeAdapter(Date.class, new CSharpDateSerializer())
                .registerTypeAdapter(Date.class, new CSharpDateDeserializer())
                .create();
  }

  @Provides @Singleton IEndpoint provideEndpoint(Gson gson, OkHttpClient httpClient) {
    return new Retrofit.Builder()
                  .baseUrl(baseEndpointURL)
                  .client(httpClient)
                  .addConverterFactory(GsonConverterFactory.createWithGson(gson))
                  .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                  .build()
                  .create(IEndpoint.class);
  }

  @Provides @Singleton ICloud provideCloud(Gson gson, OkHttpClient httpClient) {
    return new Retrofit.Builder()
                  .baseUrl(baseCloudURL)
                  .client(httpClient)
                  .addConverterFactory(GsonConverterFactory.createWithGson(gson))
                  .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                  .build()
                  .create(ICloud.class);
  }

  @Provides @Singleton IFileManager provideFileManager(IPrefManager prefManager, IDatabaseManager dbManager, IEndpoint endpoint, ICloud cloud) {
    return new FileManager(context, prefManager, dbManager, endpoint, cloud);
  }

  @Provides @Singleton Context provideContext() {
    return context;
  }

  private static SharedPreferences defaultSharedPreferences(Context context) {
    return context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
  }
}
