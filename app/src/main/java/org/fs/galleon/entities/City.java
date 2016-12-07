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

public final class City extends AbstractEntity {

  @SerializedName(value = GsonEntity.City.KEY_CITY_ID)
  private int cityId;
  @SerializedName(value = GsonEntity.City.KEY_COUNTRY_ID)
  private int countryId;
  @SerializedName(value = GsonEntity.City.KEY_CITY_NAME)
  private String cityName;

  public City() {/*default constructor*/}

  public City(Parcel input) {
    super(input);
  }

  public int getCityId() {
    return cityId;
  }

  public int getCountryId() {
    return countryId;
  }

  public String getCityName() {
    return cityName;
  }

  @Override protected void readParcel(Parcel input) {
    cityId = input.readInt();
    countryId = input.readInt();
    boolean hasCityName = input.readInt() == 1;
    if (hasCityName) {
      cityName = input.readString();
    }
  }

  @Override protected String getClassTag() {
    return City.class.getSimpleName();
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
    boolean hasCityName = !StringUtility.isNullOrEmpty(cityName);
    out.writeInt(hasCityName ? 1 : 0);
    if (hasCityName) {
      out.writeString(cityName);
    }
  }

  public final static Creator<City> CREATOR = new Creator<City>() {

    @Override public City createFromParcel(Parcel input) {
      return new City(input);
    }

    @Override public City[] newArray(int size) {
      return new City[size];
    }
  };
}