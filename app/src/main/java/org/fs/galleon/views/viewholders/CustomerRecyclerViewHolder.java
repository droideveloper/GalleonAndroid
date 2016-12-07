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
package org.fs.galleon.views.viewholders;

import android.view.View;
import android.widget.TextView;
import java.util.Locale;
import org.fs.common.BusManager;
import org.fs.core.AbstractRecyclerViewHolder;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.R;
import org.fs.galleon.entities.Customer;
import org.fs.galleon.events.CustomerSelectedEvent;
import org.fs.util.StringUtility;
import org.fs.util.ViewUtility;

public final class CustomerRecyclerViewHolder extends AbstractRecyclerViewHolder<Customer>
    implements View.OnClickListener {

  private Customer data;
  private final TextView txtCustomerName;
  private final TextView txtIndentityNo;

  public CustomerRecyclerViewHolder(View view) {
    super(view);
    view.setOnClickListener(this);
    txtCustomerName = ViewUtility.findViewById(view, R.id.txtCustomerName);
    txtIndentityNo = ViewUtility.findViewById(view, R.id.txtIdentityNo);
  }

  @Override public void onBindView(Customer data) {
    this.data = data;
    txtCustomerName.setText(String.format(Locale.getDefault(), "%s %s %s", data.getFirstName(),
        StringUtility.isNullOrEmpty(data.getMiddleName()) ? StringUtility.EMPTY : data.getMiddleName(),
        data.getLastName()));
    txtIndentityNo.setText(data.getIdentityNo());
  }

  @Override public void onClick(View v) {
    BusManager.send(new CustomerSelectedEvent(data));
  }

  @Override protected String getClassTag() {
    return CustomerRecyclerViewHolder.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }
}