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

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.fs.core.AbstractRecyclerAdapter;
import org.fs.exception.AndroidException;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.R;
import org.fs.galleon.entities.Customer;
import org.fs.galleon.views.viewholders.CustomerRecyclerViewHolder;

public class CustomerRecyclerAdapter
    extends AbstractRecyclerAdapter<Customer, CustomerRecyclerViewHolder> {

  public CustomerRecyclerAdapter(Context context) {
    this(new ArrayList<>(), context);
  }

  public CustomerRecyclerAdapter(List<Customer> dataSet, Context context) {
    super(dataSet, context);
  }

  @Override public CustomerRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final LayoutInflater factory = inflaterFactory();
    if (factory != null) {
      final View view = factory.inflate(R.layout.view_customer, parent, false);
      return new CustomerRecyclerViewHolder(view);
    } else {
      throw new AndroidException(
          String.format(Locale.ENGLISH, "LayoutInflater instance is null, %s", LayoutInflater.class.getSimpleName())
      );
    }
  }

  @Override public void onBindViewHolder(CustomerRecyclerViewHolder holder, int position) {
    final Customer customer = getItemAtIndex(position);
    if (customer != null) {
      holder.onBindView(customer);
    } else {
      log(Log.WARN,
          String.format(Locale.ENGLISH, "customer : %d in index is null.", position)
      );
    }
  }

  public final boolean requiresClear() {
    return dataSet != null && dataSet.size() > 0;
  }

  public final void clearAll() {
    if (dataSet != null) {
      dataSet.clear();
      notifyDataSetChanged();//this is done because we do not care what happened to what.
    }
  }

  @Override public int getItemViewType(int position) {
    return 0;
  }

  @Override protected String getClassTag() {
    return CustomerRecyclerAdapter.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }
}