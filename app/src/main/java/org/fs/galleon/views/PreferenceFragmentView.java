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
package org.fs.galleon.views;

import android.os.Bundle;
import org.fs.common.BusManager;
import org.fs.common.IPresenter;
import org.fs.core.AbstractPreferenceFragment;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.R;
import org.fs.galleon.events.TitleEvent;

public class PreferenceFragmentView extends AbstractPreferenceFragment<IPresenter>
    implements IPreferenceFragmentView {

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.preferences);
  }

  @Override public void onStart() {
    super.onStart();
    String titleStr = getString(R.string.titleNavigationSettings);
    BusManager.send(new TitleEvent(titleStr));
  }

  @Override protected String getClassTag() {
    return PreferenceFragmentView.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }

  @Override public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    //no-opt
  }
}