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

public final class Session extends AbstractEntity {

  @SerializedName(value = GsonEntity.Session.KEY_TOKEN)
  private String token;

  public Session() {/*default constructor*/}

  public Session(Parcel input) {
    super(input);
  }

  public String getToken() {
    return token;
  }

  @Override protected void readParcel(Parcel input) {
    boolean hasToken = input.readInt() == 1;
    if (hasToken) {
      token = input.readString();
    }
  }

  @Override protected String getClassTag() {
    return Session.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return AbstractApplication.isDebug();
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel out, int flags) {
    boolean hasToken = !StringUtility.isNullOrEmpty(token);
    out.writeInt(hasToken ? 1 : 0);
    if (hasToken) {
      out.writeString(token);
    }
  }

  public final static Creator<Session> CREATOR = new Creator<Session>() {

    @Override public Session createFromParcel(Parcel input) {
      return new Session(input);
    }

    @Override public Session[] newArray(int size) {
      return new Session[size];
    }
  };
}