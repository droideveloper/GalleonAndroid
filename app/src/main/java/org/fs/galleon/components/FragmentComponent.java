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
import org.fs.galleon.modules.FragmentModule;
import org.fs.galleon.views.ApproveDialogFragmentView;
import org.fs.galleon.views.ContactFragmentView;
import org.fs.galleon.views.PreviewProcessFragmentView;
import org.fs.galleon.views.SearchFragmentView;
import org.fs.galleon.views.ToolsFragmentView;

@PerFragment
@Component(dependencies = ApplicationComponent.class, modules = FragmentModule.class)
public interface FragmentComponent {

  void inject(SearchFragmentView fragment);
  void inject(ToolsFragmentView fragment);
  void inject(ContactFragmentView fragment);
  void inject(ApproveDialogFragmentView dialogFragment);
  void inject(PreviewProcessFragmentView fragment);
}
