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
package org.fs.galleon.commons;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Locale;

public final class CSharpDateSerializer implements JsonSerializer<Date> {

  private final static String ToJsonFormat = "\\/Date(%d)\\/";

  @Override public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
    return src == null ? null : new JsonPrimitive(
      toStr(src.getTime())
    );
  }

  private String toStr(long time) {
    return String.format(Locale.ENGLISH, ToJsonFormat, time);
  }
}
