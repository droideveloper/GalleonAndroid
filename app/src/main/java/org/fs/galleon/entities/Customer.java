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
package org.fs.galleon.entities;

import android.os.Parcel;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import org.fs.core.AbstractEntity;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.utils.GsonEntity;
import org.fs.util.Collections;
import org.fs.util.StringUtility;

public final class Customer extends AbstractEntity {

  @SerializedName(value = GsonEntity.Customer.KEY_CUSTOMER_ID)
  private int    customerId;
  @SerializedName(value = GsonEntity.Customer.KEY_FIRST_NAME)
  private String firstName;
  @SerializedName(value = GsonEntity.Customer.KEY_MIDDLE_NAME)
  private String middleName;
  @SerializedName(value = GsonEntity.Customer.KEY_LAST_NAME)
  private String lastName;
  @SerializedName(value = GsonEntity.Customer.KEY_IDENTITY_NO)
  private String identityNo;
  @SerializedName(value = GsonEntity.Customer.KEY_CATEGORY_ID)
  private int categoryId;
  @SerializedName(value = GsonEntity.Customer.KEY_CATEGORY)
  private Category category;
  @SerializedName(value = GsonEntity.Customer.KEY_CONTACTS)
  private List<Contact> contacts;

  public Customer() {/*default constructor*/}

  public Customer(Parcel input) {
    super(input);
  }

  @Override protected void readParcel(Parcel input) {
    customerId = input.readInt();
    boolean hasFirstName = input.readInt() == 1;
    if (hasFirstName) {
      firstName = input.readString();
    }
    boolean hasMiddleName = input.readInt() == 1;
    if (hasMiddleName) {
      middleName = input.readString();
    }
    boolean hasLastName = input.readInt() == 1;
    if (hasLastName) {
      lastName = input.readString();
    }
    boolean hasIdentityNo = input.readInt() == 1;
    if (hasIdentityNo) {
      identityNo = input.readString();
    }
    categoryId = input.readInt();
    boolean hasCategory = input.readInt() == 1;
    if (hasCategory) {
      category = input.readParcelable(Category.class.getClassLoader());
    }
    boolean hasContacts = input.readInt() == 1;
    if (hasContacts) {
      contacts = new ArrayList<>();
      input.readTypedList(contacts, Contact.CREATOR);
    }
  }

  public int getCustomerId() {
    return customerId;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getIdentityNo() {
    return identityNo;
  }

  public int getCategoryId() {
    return categoryId;
  }

  public Category getCategory() {
    return category;
  }

  public List<Contact> getContacts() {
    return contacts;
  }

  @Override protected String getClassTag() {
    return Customer.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel out, int flags) {
    out.writeInt(customerId);
    boolean hasFirstName = !StringUtility.isNullOrEmpty(firstName);
    out.writeInt(hasFirstName ? 1 : 0);
    if (hasFirstName) {
      out.writeString(firstName);
    }
    boolean hasMiddleName = !StringUtility.isNullOrEmpty(middleName);
    out.writeInt(hasMiddleName ? 1 : 0);
    if (hasMiddleName) {
      out.writeString(middleName);
    }
    boolean hasLastName = !StringUtility.isNullOrEmpty(lastName);
    out.writeInt(hasLastName ? 1 : 0);
    if (hasLastName) {
      out.writeString(lastName);
    }
    boolean hasIdentityNo = !StringUtility.isNullOrEmpty(identityNo);
    out.writeInt(hasIdentityNo ? 1 : 0);
    if (hasIdentityNo) {
      out.writeString(identityNo);
    }
    out.writeInt(categoryId);
    boolean hasCategory = !StringUtility.isNullOrEmpty(category);
    out.writeInt(hasCategory ? 1 : 0);
    if (hasCategory) {
      out.writeParcelable(category, flags);
    }
    boolean hasContacts = !Collections.isNullOrEmpty(contacts);
    out.writeInt(hasContacts ? 1 : 0);
    if (hasContacts) {
      out.writeTypedList (contacts);
    }
  }

  /**
   * compares customerId and identityNo fields
   * if those two are equal we accept those two object as same
   * @param o object to search
   * @return true or false a boolean flag
   */
  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Customer customer = (Customer) o;
    return customerId == customer.getCustomerId()
        && identityNo.equals(customer.getIdentityNo());
  }

  public final static Creator<Customer> CREATOR = new Creator<Customer>() {

    @Override public Customer createFromParcel(Parcel input) {
      return new Customer(input);
    }

    @Override public Customer[] newArray(int size) {
      return new Customer[size];
    }
  };
}