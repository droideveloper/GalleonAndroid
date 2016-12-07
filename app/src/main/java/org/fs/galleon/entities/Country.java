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

public final class Country extends AbstractEntity {

  @SerializedName(value = GsonEntity.Country.KEY_COUNTRY_ID)
  private int countryId;
  @SerializedName(value = GsonEntity.Country.KEY_COUNTRY_NAME)
  private String countryName;

  public Country() {/*default constructor*/}

  public Country(Parcel input) {
    super(input);
  }

  public int getCountryId() {
    return countryId;
  }

  public String getCountryName() {
    return countryName;
  }

  @Override protected void readParcel(Parcel input) {
    countryId = input.readInt();
    boolean hasCountryName = input.readInt() == 1;
    if (hasCountryName) {
      countryName = input.readString();
    }
  }

  @Override protected String getClassTag() {
    return Country.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return AbstractApplication.isDebug();
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel out, int flags) {
    out.writeInt(countryId);
    boolean hasCountryName = !StringUtility.isNullOrEmpty(countryName);
    out.writeInt(hasCountryName ? 1 : 0);
    if (hasCountryName) {
      out.writeString(countryName);
    }
  }

  public final static Creator<Country> CREATOR = new Creator<Country>() {

    @Override public Country createFromParcel(Parcel input) {
      return new Country(input);
    }

    @Override public Country[] newArray(int size) {
      return new Country[size];
    }
  };
}