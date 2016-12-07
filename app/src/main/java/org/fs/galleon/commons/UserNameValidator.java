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

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.fs.data.IValidator;
import org.fs.data.ValidationResult;

public class UserNameValidator implements IValidator<String> {

  private final static String REGEX = "^(([^<>()\\[\\]\\.,;:\\s@']+(\\.[^<>()\\[\\]\\.,;:\\s@']+)*)|('.+'))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";

  @Override public ValidationResult<String> validate(String value, Locale currentLocale) {
    Pattern pattern = Pattern.compile(REGEX);
    Matcher match = pattern.matcher(value);
    return match.find() ? new ValidationResult<>(value, ValidationResult.VALID) : ValidationResult.invalid();
  }

  public ValidationResult<String> validate(String value) {
    return validate(value, Locale.getDefault());
  }
}
