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
package org.fs.galleon.presenters;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import org.fs.common.IPresenter;

public interface ILoginActivityPresenter extends IPresenter {

  void restoreState(Bundle restoreState);
  void storeState(Bundle storeState);

  void observeUsernameChange(TextView usernameTextView);
  void observePasswordChange(TextView passwordTextView);
  void observeRememberChange(CheckBox rememberMeCheckbox);
  void observeLoginClick(Button loginButton);
}
