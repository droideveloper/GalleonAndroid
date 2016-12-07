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
import java.util.Date;
import org.fs.core.AbstractEntity;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.utils.GsonEntity;
import org.fs.util.StringUtility;

public final class Document extends AbstractEntity {

  @SerializedName(value = GsonEntity.Document.KEY_DOCUMENT_ID)
  private int documentId;
  @SerializedName(value = GsonEntity.Document.KEY_DIRECTORY_ID)
  private int directoryId;
  @SerializedName(value = GsonEntity.Document.KEY_CUSTOMER_ID)
  private int customerId;
  @SerializedName(value = GsonEntity.Document.KEY_DOCUMENT_NAME)
  private String documentName;
  @SerializedName(value = GsonEntity.Document.KEY_CONTENT_TYPE)
  private String contentType;
  @SerializedName(value = GsonEntity.Document.KEY_CONTENT_LENGTH)
  private long contentLength;
  @SerializedName(value = GsonEntity.Document.KEY_CREATE_DATE)
  private Date createDate;
  @SerializedName(value = GsonEntity.Document.KEY_UPDATE_DATE)
  private Date updateDate;

  public Document(int documentId, int directoryId, int customerId, String documentName, String contentType, long contentLength, Date createDate, Date updateDate) {
    this.documentId = documentId;
    this.directoryId = directoryId;
    this.customerId = customerId;
    this.documentName = documentName;
    this.contentType = contentType;
    this.contentLength = contentLength;
    this.createDate = createDate;
    this.updateDate = updateDate;
  }

  public Document() {/*default constructor*/}

  public Document(Parcel input) {
    super(input);
  }

  public int getDocumentId() {
    return documentId;
  }

  public int getDirectoryId() {
    return directoryId;
  }

  public int getCustomerId() {
    return customerId;
  }

  public String getDocumentName() {
    return documentName;
  }

  public String getContentType() {
    return contentType;
  }

  public long getContentLength() {
    return contentLength;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public Date getUpdateDate() {
    return updateDate;
  }

  public Builder newBuilder() {
    return new Builder()
        .documentId(documentId)
        .directoryId(directoryId)
        .customerId(customerId)
        .documentName(documentName)
        .contentType(contentType)
        .contentLength(contentLength)
        .createDate(createDate)
        .updateDate(updateDate);
  }

  @Override protected void readParcel(Parcel input) {
    documentId = input.readInt();
    directoryId = input.readInt();
    customerId = input.readInt();
    boolean hasDocumentName = input.readInt() == 1;
    if (hasDocumentName) {
      documentName = input.readString();
    }
    boolean hasContentType = input.readInt() == 1;
    if (hasContentType) {
      contentType = input.readString();
    }
    contentLength = input.readLong();
    boolean hasCreateDate = input.readInt() == 1;
    if (hasCreateDate) {
      createDate = new Date(input.readLong());
    }
    boolean hasUpdateDate = input.readInt() == 1;
    if (hasUpdateDate) {
      updateDate = new Date(input.readLong());
    }
  }

  @Override public boolean equals(Object o) {
    if (o == this) return true;
    if (o == null || o.getClass() != getClass()) return false;
    Document document = (Document) o;
    return document.getContentLength() == getContentLength()
        && document.getDocumentId() == getDocumentId();
  }

  @Override protected String getClassTag() {
    return Document.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel out, int flags) {
    out.writeInt(documentId);
    out.writeInt(directoryId);
    out.writeInt(customerId);
    boolean hasDocumentName = !StringUtility.isNullOrEmpty(documentName);
    out.writeInt(hasDocumentName ? 1 : 0);
    if (hasDocumentName) {
      out.writeString(documentName);
    }
    boolean hasContentType = !StringUtility.isNullOrEmpty(contentType);
    out.writeInt(hasContentType ? 1 : 0);
    if (hasContentType) {
      out.writeString(contentType);
    }
    out.writeLong(contentLength);
    boolean hasCreateDate = createDate.getTime() != 0L;
    out.writeInt(hasCreateDate ? 1 : 0);
    if (hasCreateDate) {
      out.writeLong(createDate.getTime());
    }
    boolean hasUpdateDate = updateDate.getTime() != 0L;
    out.writeInt(hasUpdateDate ? 1 : 0);
    if (hasUpdateDate) {
      out.writeLong(updateDate.getTime());
    }
  }

  public final static Creator<Document> CREATOR = new Creator<Document>() {

    @Override public Document createFromParcel(Parcel input) {
      return new Document(input);
    }

    @Override public Document[] newArray(int size) {
      return new Document[size];
    }
  };

  public static class Builder {
    private int documentId;
    private int directoryId;
    private int customerId;
    private String documentName;
    private String contentType;
    private long contentLength;
    private Date createDate;
    private Date updateDate;

    public Builder() { }
    public Builder documentId(int documentId) { this.documentId = documentId; return this; }
    public Builder directoryId(int directoryId) { this.directoryId = directoryId; return this; }
    public Builder customerId(int customerId) { this.customerId = customerId; return this; }
    public Builder documentName(String documentName) { this.documentName = documentName; return this; }
    public Builder contentType(String contentType) { this.contentType = contentType; return this; }
    public Builder contentLength(long contentLength) { this.contentLength = contentLength; return this; }
    public Builder createDate(Date createDate) { this.createDate = createDate; return this; }
    public Builder updateDate(Date updateDate) { this.updateDate = updateDate; return this; }
    public Document build() {
      return new Document(documentId, directoryId, customerId, documentName, contentType, contentLength, createDate, updateDate);
    }
  }
}