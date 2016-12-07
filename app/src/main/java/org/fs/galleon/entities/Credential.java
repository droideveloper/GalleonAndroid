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

public final class Credential extends AbstractEntity {

  @SerializedName(value = GsonEntity.Credential.KEY_USER_NAME)
  private String userName;
  @SerializedName(value = GsonEntity.Credential.KEY_PASSWORD)
  private String password;

  private Credential(String userName, String password) {
    this.userName = userName;
    this.password = password;
  }

  public Credential() {/*default constructor*/}

  public Credential(Parcel input) {
    super(input);
  }

  public String getUserName() {
    return userName;
  }

  public String getPassword() {
    return password;
  }

  public Builder newBuilder() {
    return new Builder().userName(userName)
                        .password(password);
  }

  @Override protected void readParcel(Parcel input) {
    boolean hasUserName = input.readInt() == 1;
    if (hasUserName) {
      userName = input.readString();
    }
    boolean hasPassword = input.readInt() == 1;
    if (hasPassword) {
      password = input.readString();
    }
  }

  @Override protected String getClassTag() {
    return Credential.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return AbstractApplication.isDebug();
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel out, int flags) {
    boolean hasUserName = !StringUtility.isNullOrEmpty(userName);
    out.writeInt(hasUserName ? 1 : 0);
    if (hasUserName) {
      out.writeString(userName);
    }
    boolean hasPassword = !StringUtility.isNullOrEmpty(password);
    out.writeInt(hasPassword ? 1 : 0);
    if (hasPassword) {
      out.writeString(password);
    }
  }

  public final static Creator<Credential> CREATOR = new Creator<Credential>() {

    @Override public Credential createFromParcel(Parcel input) {
      return new Credential(input);
    }

    @Override public Credential[] newArray(int size) {
      return new Credential[size];
    }
  };

  //builder for Credential
  public static class Builder {
    private String userName;
    private String password;

    public Builder() { }
    public Builder userName(String userName) { this.userName = userName; return this; }
    public Builder password(String password) { this.password = password; return this; }
    public Credential build() {
      return new Credential(userName, password);
    }
  }
}