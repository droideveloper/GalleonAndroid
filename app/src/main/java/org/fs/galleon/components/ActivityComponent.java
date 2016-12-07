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
package org.fs.galleon.components;

import dagger.Component;
import org.fs.galleon.modules.ActivityModule;
import org.fs.galleon.views.CustomerDetailActivityView;
import org.fs.galleon.views.LoginActivityView;
import org.fs.galleon.views.MainActivityView;
import org.fs.galleon.views.PdfActivityView;

@PerActivity
@Component(modules = ActivityModule.class, dependencies = ApplicationComponent.class)
public interface ActivityComponent {

  void inject(LoginActivityView activity);
  void inject(MainActivityView activity);
  void inject(CustomerDetailActivityView activity);
  void inject(PdfActivityView activity);
}
