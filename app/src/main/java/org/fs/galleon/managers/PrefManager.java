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
package org.fs.galleon.managers;

import android.content.SharedPreferences;
import java.util.Locale;
import java.util.Map;
import org.fs.common.AbstractManager;
import org.fs.exception.AndroidException;
import org.fs.galleon.GalleonApplication;

public final class PrefManager extends AbstractManager implements IPrefManager {

  private final SharedPreferences prefManager;

  public final static String KEY_USERNAME     = "userName";
  public final static String KEY_PASSWORD     = "password";
  public final static String KEY_REMEMBER_ME  = "rememberMe";
  public final static String KEY_AUTO_SYNC    = "autoSync";
  public final static String KEY_SYNC_FOLDER  = "syncFolder";
  public final static String KEY_TOOLS_FOLDER = "toolsFolder";
  public final static String KEY_AUTH_TOKEN   = "authToken";

  public PrefManager(SharedPreferences prefManager) {
    this.prefManager = prefManager;
    loadDefaultsIfFirstLaunch();
  }

  @Override protected String getClassTag() {
    return PrefManager.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }

  @Override public boolean hasKey(String key) {
    return prefManager != null && prefManager.contains(key);
  }

  @Override public <T> void setValue(String key, T value) {
    SharedPreferences.Editor editor = prefManager.edit();
    try {
      if (value instanceof String) {
        editor.putString(key, (String) value);
      } else if(value instanceof Boolean) {
        editor.putBoolean(key, (Boolean) value);
      } else if(value instanceof Float) {
        editor.putFloat(key, (Float) value);
      } else if(value instanceof Integer) {
        editor.putInt(key, (Integer) value);
      } else if(value instanceof Long) {
        editor.putLong(key, (Long) value);
      } else {
        throw new AndroidException(
            String.format(Locale.ENGLISH, "class '%s' is not supported.", value.getClass().getSimpleName())
        );
      }
    } finally {
      editor.apply();
    }
  }

  @Override public <T> T getValue(String key, T defaultValue) {
    try {
      if (hasKey(key)) {
        Map<String, ?> keysAndValues = prefManager.getAll();
        return (T) keysAndValues.get(key);
      } return defaultValue;
    } catch (ClassCastException castError) {
      castError.printStackTrace();
      return defaultValue;
    }
  }

  //we need to handle it by hand here because those values will not stay here
  //since then providing default when unknown is required.
  private void loadDefaultsIfFirstLaunch() {
    Map<String, ?> defaults = prefManager.getAll();
    if (!defaults.containsKey(KEY_SYNC_FOLDER)) {
      setValue(KEY_SYNC_FOLDER, "/sync");
    }
    if (!defaults.containsKey(KEY_TOOLS_FOLDER)) {
      setValue(KEY_TOOLS_FOLDER, "/tools");
    }
  }
}