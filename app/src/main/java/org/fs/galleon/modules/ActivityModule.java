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

import dagger.Module;
import dagger.Provides;
import org.fs.galleon.components.PerActivity;
import org.fs.galleon.managers.IDatabaseManager;
import org.fs.galleon.managers.IFileManager;
import org.fs.galleon.managers.IPrefManager;
import org.fs.galleon.nets.ICloud;
import org.fs.galleon.nets.IEndpoint;
import org.fs.galleon.presenters.CustomerDetailActivityPresenter;
import org.fs.galleon.presenters.ICustomerDetailActivityPresenter;
import org.fs.galleon.presenters.ILoginActivityPresenter;
import org.fs.galleon.presenters.IMainActivityPresenter;
import org.fs.galleon.presenters.IPdfActivityPresenter;
import org.fs.galleon.presenters.IPickUploadActivityPresenter;
import org.fs.galleon.presenters.LoginActivityPresenter;
import org.fs.galleon.presenters.MainActivityPresenter;
import org.fs.galleon.presenters.PdfActivityPresenter;
import org.fs.galleon.presenters.PickUploadActivityPresenter;
import org.fs.galleon.views.ICustomerDetailActivityView;
import org.fs.galleon.views.ILoginActivityView;
import org.fs.galleon.views.IMainActivityView;
import org.fs.galleon.views.IPdfActivityView;
import org.fs.galleon.views.IPickUploadActivityView;

@Module
public class ActivityModule {

  private final Object view;

  public ActivityModule(Object view) {
    this.view = view;
  }

  @Provides @PerActivity ILoginActivityPresenter provideLoginPresenter(IPrefManager prefManager, IEndpoint endpoint) {
    return new LoginActivityPresenter((ILoginActivityView) view, prefManager, endpoint);
  }

  @Provides @PerActivity IMainActivityPresenter provideMainPresenter(IEndpoint endpoint, IFileManager fileManager, IPrefManager prefManager) {
    return new MainActivityPresenter((IMainActivityView) view, endpoint, fileManager, prefManager);
  }

  @Provides @PerActivity ICustomerDetailActivityPresenter provideCustomerDetailPresenter(IEndpoint endpoint, IDatabaseManager dbManager, ICloud cloud, IFileManager fileManager) {
    return new CustomerDetailActivityPresenter((ICustomerDetailActivityView) view, endpoint, dbManager, cloud, fileManager);
  }

  @Provides @PerActivity IPdfActivityPresenter providePdfActivityPresenter() {
    return new PdfActivityPresenter((IPdfActivityView) view);
  }

  @Provides @PerActivity IPickUploadActivityPresenter providePickUploadActivityPresenter(IFileManager fileManager, IPrefManager prefManager) {
    return new PickUploadActivityPresenter((IPickUploadActivityView) view, fileManager, prefManager);
  }
}
