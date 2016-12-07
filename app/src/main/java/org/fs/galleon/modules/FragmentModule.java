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
import org.fs.galleon.components.PerFragment;
import org.fs.galleon.managers.IFileManager;
import org.fs.galleon.presenters.ApproveDialogFragmentPresenter;
import org.fs.galleon.presenters.ContactFragmentPresenter;
import org.fs.galleon.presenters.IApproveDialogFragmentPresenter;
import org.fs.galleon.presenters.IContactFragmentPresenter;
import org.fs.galleon.presenters.IPreviewProcessFragmentPresenter;
import org.fs.galleon.presenters.ISearchFragmentPresenter;
import org.fs.galleon.presenters.IToolsFragmentPresenter;
import org.fs.galleon.presenters.PreviewProcessFragmentPresenter;
import org.fs.galleon.presenters.SearchFragmentPresenter;
import org.fs.galleon.presenters.ToolsFragmentPresenter;
import org.fs.galleon.views.IApproveDialogFragmentView;
import org.fs.galleon.views.IContactFragmentView;
import org.fs.galleon.views.IPreviewProcessFragmentView;
import org.fs.galleon.views.ISearchFragmentView;
import org.fs.galleon.views.IToolsFragmentView;

@Module
public class FragmentModule {

  private final Object view;

  public FragmentModule(final Object view) {
    this.view = view;
  }

  @Provides @PerFragment ISearchFragmentPresenter provideSearchFragmentPresenter() {
    return new SearchFragmentPresenter((ISearchFragmentView) view);
  }

  @Provides @PerFragment IContactFragmentPresenter provideContactFragmentPresenter() {
    return new ContactFragmentPresenter((IContactFragmentView) view);
  }

  @Provides @PerFragment IApproveDialogFragmentPresenter provideApproveDialogFragmentPresenter() {
    return new ApproveDialogFragmentPresenter((IApproveDialogFragmentView) view);
  }

  @Provides @PerFragment IToolsFragmentPresenter provideToolsFragmentPresenter(IFileManager fileManager) {
    return new ToolsFragmentPresenter((IToolsFragmentView) view, fileManager);
  }

  @Provides @PerFragment IPreviewProcessFragmentPresenter providePreviewProcessFragmentPresenter() {
    return new PreviewProcessFragmentPresenter((IPreviewProcessFragmentView) view);
  }
}
