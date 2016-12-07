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
import java.util.List;
import org.fs.core.AbstractApplication;
import org.fs.core.AbstractEntity;
import org.fs.galleon.utils.GsonEntity;
import org.fs.util.Collections;
import org.fs.util.StringUtility;

public final class Directory extends AbstractEntity {

  @SerializedName(value = GsonEntity.Directory.KEY_DIRECTORY_ID)
  private int directoryId;
  @SerializedName(value = GsonEntity.Directory.KEY_PARENT_DIRECTORY_ID)
  private int parentDirectoryId;
  @SerializedName(value = GsonEntity.Directory.KEY_CUSTOMER_ID)
  private int customerId;
  @SerializedName(value = GsonEntity.Directory.KEY_DIRECTORY_NAME)
  private String directoryName;
  @SerializedName(value = GsonEntity.Directory.KEY_PARENT)
  private Directory parentDirectory;
  @SerializedName(value = GsonEntity.Directory.KEY_DOCUMENTS)
  private List<Document> documents;

  public Directory() {/*default constructor*/}

  public Directory(Parcel input) {
    super(input);
  }

  public int getDirectoryId() {
    return directoryId;
  }

  public int getParentDirectoryId() {
    return parentDirectoryId;
  }

  public int getCustomerId() {
    return customerId;
  }

  public String getDirectoryName() {
    return directoryName;
  }

  public Directory getParentDirectory() {
    return parentDirectory;
  }

  public List<Document> getDocuments() {
    return documents;
  }

  @Override protected void readParcel(Parcel input) {
    directoryId = input.readInt();
    parentDirectoryId = input.readInt();
    customerId = input.readInt();
    boolean hasDirectoryName = input.readInt() == 1;
    if (hasDirectoryName) {
      directoryName = input.readString();
    }
    boolean hasParentDirectory = input.readInt() == 1;
    if (hasParentDirectory) {
      parentDirectory = input.readParcelable(Directory.class.getClassLoader());
    }
    boolean hasDocuments = input.readInt() == 1;
    if (hasDocuments) {
      input.readTypedList(documents, Document.CREATOR);
    }
  }

  @Override protected String getClassTag() {
    return Directory.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return AbstractApplication.isDebug();
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel out, int flags) {
    out.writeInt(directoryId);
    out.writeInt(parentDirectoryId);
    out.writeInt(customerId);
    boolean hasDirectoryName = !StringUtility.isNullOrEmpty(directoryName);
    out.writeInt(hasDirectoryName ? 1 : 0);
    if (hasDirectoryName) {
      out.writeString(directoryName);
    }
    boolean hasParentDirectory = !StringUtility.isNullOrEmpty(parentDirectory);
    out.writeInt(hasParentDirectory ? 1 : 0);
    if (hasParentDirectory) {
      out.writeParcelable(parentDirectory, flags);
    }
    boolean hasDocuments = !Collections.isNullOrEmpty(documents);
    out.writeInt(hasDocuments ? 1 : 0);
    if (hasDocuments) {
      out.writeTypedList(documents);
    }
  }

  public final static Creator<Directory> CREATOR = new Creator<Directory>() {

    @Override public Directory createFromParcel(Parcel input) {
      return new Directory(input);
    }

    @Override public Directory[] newArray(int size) {
      return new Directory[size];
    }
  };
}