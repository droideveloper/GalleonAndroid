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
import org.fs.core.AbstractEntity;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.utils.GsonEntity;
import org.fs.util.StringUtility;

public final class Category extends AbstractEntity {

  @SerializedName(value = GsonEntity.Category.KEY_CATEGORY_ID)
  private int categoryId;
  @SerializedName(value = GsonEntity.Category.KEY_CATEGORY_NAME)
  private String categoryName;

  public Category() {/*default constructor*/}

  public Category(Parcel input) {
    super(input);
  }

  public int getCategoryId() {
    return categoryId;
  }

  public String getCategoryName() {
    return categoryName;
  }

  @Override protected void readParcel(Parcel input) {
    categoryId = input.readInt();
    boolean hasCategoryName = input.readInt() == 1;
    if (hasCategoryName) {
      categoryName = input.readString();
    }
  }

  @Override protected String getClassTag() {
    return Category.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel out, int flags) {
    out.writeInt(categoryId);
    boolean hasCategoryName = !StringUtility.isNullOrEmpty(categoryName);
    out.writeInt(hasCategoryName ? 1 : 0);
    if (hasCategoryName) {
      out.writeString(categoryName);
    }
  }

  public final static Creator<Category> CREATOR = new Creator<Category>() {

    @Override public Category createFromParcel(Parcel input) {
      return new Category(input);
    }

    @Override public Category[] newArray(int size) {
      return new Category[size];
    }
  };
}