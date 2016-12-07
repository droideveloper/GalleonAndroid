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
import org.fs.core.AbstractApplication;
import org.fs.core.AbstractEntity;
import org.fs.galleon.utils.GsonEntity;
import org.fs.util.StringUtility;

public final class Contact extends AbstractEntity {

  @SerializedName(value = GsonEntity.Contact.KEY_CITY_ID)
  private int cityId;
  @SerializedName(value = GsonEntity.Contact.KEY_COUNTRY_ID)
  private int countryId;
  @SerializedName(value = GsonEntity.Contact.KEY_CONTACT_NAME)
  private String contactName;
  @SerializedName(value = GsonEntity.Contact.KEY_ADDRESS)
  private String address;
  @SerializedName(value = GsonEntity.Contact.KEY_CUSTOMER_ID)
  private int customerId;
  @SerializedName(value = GsonEntity.Contact.KEY_PHONE)
  private String phone;
  @SerializedName(value = GsonEntity.Contact.KEY_CITY)
  private City city;
  @SerializedName(value = GsonEntity.Contact.KEY_COUNTRY)
  private Country country;

  public Contact() {/*default constructor*/}

  private Contact(Parcel input) {
    super(input);
  }

  public int getCityId() {
    return cityId;
  }

  public int getCountryId() {
    return countryId;
  }

  public String getContactName() {
    return contactName;
  }

  public String getAddress() {
    return address;
  }

  public int getCustomerId() {
    return customerId;
  }

  public String getPhone() {
    return phone;
  }

  public City getCity() {
    return city;
  }

  public Country getCountry() {
    return country;
  }

  @Override protected void readParcel(Parcel input) {
    cityId = input.readInt();
    countryId = input.readInt();
    boolean hasContactName = input.readInt() == 1;
    if (hasContactName) {
      contactName = input.readString();
    }
    boolean hasAddress = input.readInt() == 1;
    if (hasAddress) {
      address = input.readString();
    }
    customerId = input.readInt();
    boolean hasPhone = input.readInt() == 1;
    if (hasPhone) {
      phone = input.readString();
    }
    boolean hasCity = input.readInt() == 1;
    if (hasCity) {
      city = input.readParcelable(City.class.getClassLoader());
    }
    boolean hasCountry = input.readInt() == 1;
    if (hasCountry) {
      country = input.readParcelable(Country.class.getClassLoader());
    }
  }

  @Override protected String getClassTag() {
    return Contact.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return AbstractApplication.isDebug();
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel out, int flags) {
    out.writeInt(cityId);
    out.writeInt(countryId);
    boolean hasContactName = !StringUtility.isNullOrEmpty(contactName);
    out.writeInt(hasContactName ? 1 : 0);
    if (hasContactName) {
      out.writeString(contactName);
    }
    boolean hasAddress = !StringUtility.isNullOrEmpty(address);
    out.writeInt(hasAddress ? 1 : 0);
    if (hasAddress) {
      out.writeString(address);
    }
    out.writeInt(customerId);
    boolean hasPhone = !StringUtility.isNullOrEmpty(phone);
    out.writeInt(hasPhone ? 1 : 0);
    if (hasPhone) {
      out.writeString(phone);
    }
    boolean hasCity = !StringUtility.isNullOrEmpty(city);
    out.writeInt(hasCity ? 1 : 0);
    if (hasCity) {
      out.writeParcelable(city, flags);
    }
    boolean hasCountry = !StringUtility.isNullOrEmpty(country);
    out.writeInt(hasCountry ? 1 : 0);
    if (hasCountry) {
      out.writeParcelable(country, flags);
    }
  }

  public final static Creator<Contact> CREATOR = new Creator<Contact>() {

    @Override public Contact createFromParcel(Parcel input) {
      return new Contact(input);
    }

    @Override public Contact[] newArray(int size) {
      return new Contact[size];
    }
  };
}