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

public interface IPrefManager {

  /**
   * control of key if exists
   * @param key key to look for
   * @return true if there is such key false otherwise.
   */
  boolean hasKey(String key);

  /**
   * set value for key
   * @param key key to look for
   * @param value T value for key
   * @param <T> T type of value
   */
  <T> void setValue(String key, T value);

  /**
   * get value for key if not found defaultValue will be returned.
   * @param key key to look for
   * @param defaultValue default value if not found
   * @param <T> type of value
   * @return value found for key or default if not found
   */
  <T> T getValue(String key, T defaultValue);
}
