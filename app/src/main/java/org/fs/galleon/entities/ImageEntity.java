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

import android.net.Uri;
import android.os.Parcel;
import org.fs.core.AbstractEntity;
import org.fs.galleon.GalleonApplication;
import org.fs.util.StringUtility;

public final class ImageEntity extends AbstractEntity {

  private Uri imageUri;

  private ImageEntity(Uri imageUri) {
    this.imageUri = imageUri;
  }

  public ImageEntity() {/*default constructor*/}
  private ImageEntity(Parcel input) {
    super(input);
  }

  public Uri getImageUri() {
    return imageUri;
  }

  public Builder newBuilder() {
    return new Builder().imageUri(imageUri);
  }

  @Override protected void readParcel(Parcel input) {
    boolean hasImageUri = input.readInt() == 1;
    if (hasImageUri) {
      imageUri = Uri.parse(input.readString());
    }
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ImageEntity entity = (ImageEntity) o;
    return getImageUri().equals(entity.getImageUri());
  }

  @Override protected String getClassTag() {
    return ImageEntity.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel out, int flags) {
    boolean hasImageUri = !StringUtility.isNullOrEmpty(imageUri);
    out.writeInt(hasImageUri ? 1 : 0);
    if (hasImageUri) {
      out.writeString(imageUri.toString());
    }
  }

  public final static Creator<ImageEntity> CREATOR = new Creator<ImageEntity>() {

    @Override public ImageEntity createFromParcel(Parcel input) {
      return new ImageEntity(input);
    }

    @Override public ImageEntity[] newArray(int size) {
      return new ImageEntity[size];
    }
  };

  //builder helper for ImageEntity.class
  public static class Builder {
    private Uri imageUri;

    public Builder() {}
    public Builder imageUri(Uri imageUri) { this.imageUri = imageUri; return this; }
    public ImageEntity build() {
      return new ImageEntity(imageUri);
    }
  }
}