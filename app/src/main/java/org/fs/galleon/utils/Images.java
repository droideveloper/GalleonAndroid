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
package org.fs.galleon.utils;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import org.fs.util.PreconditionUtility;

public final class Images {

  private Images() {
    throw new IllegalArgumentException("you can not have instance of this object");
  }

  public static String getPath(Context context, Uri uri) {
    PreconditionUtility.checkNotNull(uri, "content uri is null");
    PreconditionUtility.checkNotNull(context, "context is null");
    Cursor cursor;
    String[] projection = { MediaStore.Images.Media.DATA };
    if (isBuildSDKAvailable(Build.VERSION_CODES.KITKAT)) {
      String id = DocumentsContract.getDocumentId(uri);
      id = id.split(":")[1];
      String selection = MediaStore.Images.Media._ID + "=?";
      cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
          projection, selection, new String[] { id }, null);
    } else if (isBuildSDKAvailable(Build.VERSION_CODES.ICE_CREAM_SANDWICH)) {
      CursorLoader loader = new CursorLoader(context, uri, projection, null, null, null);
      cursor = loader.loadInBackground();
    } else {
      cursor = context.getContentResolver().query(uri, projection, null, null, null);
    }
    if (cursor != null) {
      int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
      cursor.moveToFirst();
      String path = cursor.getString(index);
      cursor.close();
      return path;
    }
    return null;
  }

  private static boolean isBuildSDKAvailable(int sdkRequired) {
    return Build.VERSION.SDK_INT >= sdkRequired;
  }
}
