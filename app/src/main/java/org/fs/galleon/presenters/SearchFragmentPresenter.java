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
package org.fs.galleon.presenters;

import android.content.Intent;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import org.fs.common.AbstractPresenter;
import org.fs.common.BusManager;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.R;
import org.fs.galleon.entities.Customer;
import org.fs.galleon.events.CustomerSelectedEvent;
import org.fs.galleon.events.SearchEvent;
import org.fs.galleon.events.SearchFoundEvent;
import org.fs.galleon.events.TitleEvent;
import org.fs.galleon.views.CustomerDetailActivityView;
import org.fs.galleon.views.ISearchFragmentView;
import org.fs.util.Collections;
import rx.Subscription;

public class SearchFragmentPresenter extends AbstractPresenter<ISearchFragmentView>
    implements ISearchFragmentPresenter {

  private final static String KEY_CUSTOMERS = "search.fragment.customers";

  private Subscription eventSub;
  private List<Customer> customers;

  public SearchFragmentPresenter(ISearchFragmentView view) {
    super(view);
  }

  @Override public void onCreate() {
    if(view.isAvailable()) {
      view.showProgress();
    }
  }

  @Override public void onStart() {
    //show searchBar
    BusManager.send(new SearchEvent(true));
    String title = view.getContext().getString(R.string.titleNavigationFindCustomers);
    BusManager.send(new TitleEvent(title));
    eventSub = BusManager.add((evt) -> {
      if (evt instanceof SearchFoundEvent) {
        SearchFoundEvent search = (SearchFoundEvent) evt;
        if(view.isAvailable()) {
          customers = search.customers();
          view.bindAdapter(customers);
          view.hideProgress();
        }
      } else if (evt instanceof CustomerSelectedEvent) {
        CustomerSelectedEvent customerSelected = (CustomerSelectedEvent) evt;
        if (view.isAvailable()) {
          Intent intent = new Intent(view.getContext(), CustomerDetailActivityView.class);
          intent.putExtra(CustomerDetailActivityPresenter.KEY_CUSTOMER_ENTITY, customerSelected.selectedCustomer());
          view.startActivity(intent);
        }
      }
    });
    //if we have previous customers then we are allowed to show it.
    if (customers != null) {
      if (view.isAvailable()) {
        view.hideProgress();
        view.bindAdapter(customers);
      }
    }
  }

  @Override public void onStop() {
    //hide searchBar
    BusManager.send(new SearchEvent(false));
    if (eventSub != null) {
      BusManager.remove(eventSub);
      eventSub = null;
    }
  }

  @Override public void restoreState(Bundle restoreState) {
    if (restoreState != null) {
      if (restoreState.containsKey(KEY_CUSTOMERS)) {
        customers = restoreState.getParcelableArrayList(KEY_CUSTOMERS);
      }
    }
  }

  @Override public void storeState(Bundle storeState) {
    if (!Collections.isNullOrEmpty(customers)) {
      storeState.putParcelableArrayList(KEY_CUSTOMERS, new ArrayList<>(customers));
    }
  }

  @Override protected String getClassTag() {
    return SearchFragmentPresenter.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }
}