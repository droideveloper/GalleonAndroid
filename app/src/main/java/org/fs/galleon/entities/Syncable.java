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
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;
import org.fs.core.AbstractEntity;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.utils.DbConstants;
import org.fs.util.StringUtility;

@DatabaseTable(tableName = DbConstants.TABLE_SYNCABLES)
public final class Syncable extends AbstractEntity {

  @DatabaseField(columnName = DbConstants.COLUMN_SYNCABLE_ID,
                 generatedId = true)
  private Long    syncableId;
  @DatabaseField(columnName = DbConstants.COLUMN_REMOTE_ID,
                 unique = true)
  private int     remoteId;
  @DatabaseField(columnName = DbConstants.COLUMN_FILE_NAME,
                 canBeNull = false)
  private String  fileName;
  @DatabaseField(columnName = DbConstants.COLUMN_LOCAL_PATH,
                 canBeNull = false)
  private String  localPath;
  @DatabaseField(columnName = DbConstants.COLUMN_LAST_MODIFIED_TIME,
                 canBeNull = false)
  private Date    lastModifiedTime;

  public Syncable() {/*default constructor*/}

  public Syncable(long id, int remoteId, String fileName, String localPath, Date lastModifiedTime) {
    this.syncableId = id;
    this.remoteId = remoteId;
    this.fileName = fileName;
    this.localPath = localPath;
    this.lastModifiedTime = lastModifiedTime;
  }
  public Syncable(Parcel input) {
    super(input);
  }

  @Override protected void readParcel(Parcel input) {
    syncableId = input.readLong();
    remoteId = input.readInt();
    boolean hasFileName = input.readInt() == 1;
    if (hasFileName) {
      fileName = input.readString();
    }
    boolean hasLocalPath = input.readInt() == 1;
    if (hasLocalPath) {
      localPath = input.readString();
    }
    boolean hasLastModifiedTime = input.readInt() == 1;
    if (hasLastModifiedTime) {
      lastModifiedTime = new Date(input.readLong());
    }
  }

  @Override protected String getClassTag() {
    return Syncable.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel out, int flags) {
    out.writeLong(syncableId);
    out.writeInt(remoteId);
    boolean hasFileName = !StringUtility.isNullOrEmpty(fileName);
    out.writeInt(hasFileName ? 1 : 0);
    if (hasFileName) {
      out.writeString(fileName);
    }
    boolean hasLocalPath = !StringUtility.isNullOrEmpty(localPath);
    out.writeInt(hasLocalPath ? 1 : 0);
    if (hasLocalPath) {
      out.writeString(localPath);
    }
    boolean hasLastModifiedTime = lastModifiedTime.getTime() != 0L;
    out.writeInt(hasLastModifiedTime ? 1 : 0);
    if (hasLastModifiedTime) {
      out.writeLong(lastModifiedTime.getTime());
    }
  }
//new builder will be created according to my file
  public Builder newBuilder() {
    return new Builder().id(syncableId)
                        .remoteId(remoteId)
                        .fileName(fileName)
                        .localPath(localPath)
                        .lastModifiedTime(lastModifiedTime);
  }

  public Long getSyncableId() {
    return syncableId;
  }

  public int getRemoteId() {
    return remoteId;
  }

  public String getFileName() {
    return fileName;
  }

  public String getLocalPath() {
    return localPath;
  }

  public Date getLastModifiedTime() {
    return lastModifiedTime;
  }

  public final static Creator<Syncable> CREATOR = new Creator<Syncable>() {

    @Override public Syncable createFromParcel(Parcel input) {
      return new Syncable(input);
    }

    @Override public Syncable[] newArray(int size) {
      return new Syncable[size];
    }
  };

  //Builder class for accessing new type
  public static class Builder {
    private long id;
    private int remoteId;
    private String fileName;
    private String localPath;
    private Date lastModifiedTime;

    public Builder() {}
    public Builder id(long id) { this.id = id; return this; }
    public Builder remoteId(int remoteId) { this.remoteId = remoteId; return this; }
    public Builder fileName(String fileName) { this.fileName = fileName; return this; }
    public Builder localPath(String localPath) { this.localPath = localPath; return this; }
    public Builder lastModifiedTime(Date lastModifiedTime) { this.lastModifiedTime = lastModifiedTime; return this; }
    public Syncable build() { return new Syncable(id, remoteId, fileName, localPath, lastModifiedTime);}
  }
}