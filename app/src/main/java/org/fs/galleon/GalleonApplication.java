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
package org.fs.galleon;

import org.fs.core.AbstractApplication;
import org.fs.galleon.components.ApplicationComponent;
import org.fs.galleon.components.DaggerApplicationComponent;
import org.fs.galleon.modules.ApplicationModule;

public class GalleonApplication extends AbstractApplication {

  //TODO update those
  private final static String ENDPOINT_URL  = "http://192.168.1.105:52192/";
  private final static String CLOUD_URL     = "http://192.168.1.105:52192/";

  private ApplicationComponent applicationComponent;

  public GalleonApplication() {
    this(BuildConfig.DEBUG);
  }

  private GalleonApplication(boolean debug) {
    super(debug);
  }

  @Override public void onCreate() {
    super.onCreate();
    applicationComponent = DaggerApplicationComponent.builder()
        .applicationModule(new ApplicationModule(this, ENDPOINT_URL, CLOUD_URL))
        .build();
  }

  public ApplicationComponent applicationComponent() {
    return applicationComponent;
  }

  protected String getClassTag() {
    return GalleonApplication.class.getSimpleName();
  }

  protected boolean isLogEnabled() {
    return isDebug();
  }

  public static boolean isDebug() {
    return BuildConfig.DEBUG;
  }
}