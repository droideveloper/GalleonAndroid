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
package org.fs.galleon.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import java.io.File;
import org.fs.core.AbstractPagerAdapter;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.views.PreviewProcessFragmentView;
import org.fs.util.IPropertyChangedListener;
import org.fs.util.ObservableList;

public class PreviewProcessStatePagerAdapter extends AbstractPagerAdapter<File> implements
    IPropertyChangedListener {

  public PreviewProcessStatePagerAdapter(FragmentManager fragmentManager, ObservableList<File> dataSet) {
    super(fragmentManager, dataSet);
    dataSet.registerPropertyChangedListener(this);
  }

  @Override protected String getClassTag() {
    return PreviewProcessStatePagerAdapter.class.getSimpleName();
  }

  @Override public CharSequence getPageTitle(int position) {
    File file = getItemAtIndex(position);
    return file != null && file.exists() ? file.getName() : super.getPageTitle(position);
  }

  //IPropertyChangedListener for next callbacks we listen to understand if any changes are occured
  @Override public void notifyItemsRemoved(int index, int size) {
    notifyDataSetChanged();
  }

  @Override public void notifyItemsInserted(int index, int size) {
    notifyDataSetChanged();
  }

  @Override public void notifyItemsChanged(int index, int size) {
    notifyDataSetChanged();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }

  @Override protected Fragment onBind(int position, File file) {
    return PreviewProcessFragmentView.newInstance(file.getAbsolutePath());
  }
}