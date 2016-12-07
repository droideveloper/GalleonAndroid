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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.fs.core.AbstractStatePagerAdapter;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.entities.Contact;
import org.fs.galleon.views.ContactFragmentView;
import org.fs.util.Collections;

public class ContactStatePagerAdapter extends AbstractStatePagerAdapter<Contact> {

  public ContactStatePagerAdapter(FragmentManager fragmentManager) {
    this(fragmentManager, new ArrayList<>());
  }

  private ContactStatePagerAdapter(FragmentManager fragmentManager, List<Contact> dataSet) {
    super(fragmentManager, dataSet);
  }

  @Override public CharSequence getPageTitle(int position) {
    final Contact contact = getItemAtIndex(position);
    if (contact != null) {
      return contact.getContactName()
          .toLowerCase(Locale.getDefault());
    }
    return super.getPageTitle(position);
  }

  public final void addAll(Collection<Contact> collection, boolean front) {
    if (!Collections.isNullOrEmpty(collection)) {
      if (front) {
        dataSet.addAll(0, collection);
      } else {
        dataSet.addAll(collection);
      }
      notifyDataSetChanged();
    }
  }

  public final void clearIfExists() {
    if (dataSet != null) {
      dataSet.clear();
      notifyDataSetChanged();
    }
  }

  @Override protected Fragment onBind(int position, Contact contact) {
    return ContactFragmentView.newInstance(contact);
  }

  @Override protected String getClassTag() {
    return ContactStatePagerAdapter.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }
}