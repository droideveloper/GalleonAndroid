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

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;
import com.jakewharton.rxbinding.view.RxView;
import java.util.Locale;
import org.fs.common.AbstractPresenter;
import org.fs.common.BusManager;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.R;
import org.fs.galleon.entities.Contact;
import org.fs.galleon.events.ApproveEvent;
import org.fs.galleon.views.ApproveDialogFragmentView;
import org.fs.galleon.views.IContactFragmentView;
import org.fs.util.StringUtility;
import rx.Subscription;

public class ContactFragmentPresenter extends AbstractPresenter<IContactFragmentView>
    implements IContactFragmentPresenter {

  private final static int PERMISSION_ALLOW_CALL = 0x10;

  public final static String KEY_CONTACT = "contact.fragment.entity";

  private Subscription eventListener;
  private Subscription txtPhoneSub;
  private Contact contact;

  public ContactFragmentPresenter(IContactFragmentView view) {
    super(view);
  }

  @Override public void onStart() {
    if (view.isAvailable()) {
      if (contact != null) {
        view.setPhone(contact.getPhone());
        view.setAddress(contact.getAddress());
        String cityName = contact.getCity() != null ? contact.getCity().getCityName() : StringUtility.EMPTY;
        String countryName = contact.getCountry() != null ? contact.getCountry().getCountryName() : StringUtility.EMPTY;
        if (!StringUtility.isNullOrEmpty(cityName)
            && !StringUtility.isNullOrEmpty(countryName)) {
          view.showCityAndCountry();
          view.setCityAndCountry(
              String.format(Locale.getDefault(), "%s, %s", cityName, countryName)
          );
        } else {
          view.hideCityAndCountry();
        }
      }
      dispatchAskPermissionsIfNeeded();
      eventListener = BusManager.add((event) -> {
        if (event instanceof ApproveEvent) {
          ApproveEvent approveEvent = (ApproveEvent) event;
          if (approveEvent.isApproved()) {
            if (view.isAvailable()) {
              Intent intent = new Intent(Intent.ACTION_CALL);
              intent.setData(Uri.parse(String.format(Locale.ENGLISH, "tel:%s", contact.getPhone())));
              view.startActivity(intent);
            }
          }
        }
      });
    }
  }

  @Override public void onStop() {
    if (txtPhoneSub != null) {
      txtPhoneSub.unsubscribe();
      txtPhoneSub = null;
    }
    if (eventListener != null) {
      BusManager.remove(eventListener);
      eventListener = null;
    }
  }

  @Override protected String getClassTag() {
    return ContactFragmentPresenter.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }

  @Override public void restoreState(Bundle restoreState) {
    if (restoreState != null) {
      if (restoreState.containsKey(KEY_CONTACT)) {
        contact = restoreState.getParcelable(KEY_CONTACT);
      }
    }
  }

  @Override public void storeState(Bundle storeState) {
    if (contact != null) {
      storeState.putParcelable(KEY_CONTACT, contact);
    }
  }

  @Override public void observeClicks(TextView txtPhone) {
    txtPhoneSub = RxView.clicks(txtPhone)
        .subscribe((x) -> {
          if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            String format = view.getContext().getString(R.string.titleShouldCallDialogMessage);
            format = String.format(Locale.getDefault(), format, contact.getPhone());
            ApproveDialogFragmentView dialog = ApproveDialogFragmentView.newInstance(format, null);
            dialog.show(view.getSupportFragmentManager(), "call_approve_dialog");
          } else {
            if (view.isAvailable()) {
              view.showError(view.getContext().getString(R.string.titleMakeCallPermissionMessage));
            }
          }
        });
  }

  @Override public void requestPermissions(int requestCode, String[] permissions, int[] results) {
    if(requestCode == PERMISSION_ALLOW_CALL) {
      if (results != null && results.length > 0) {
        if (results[0] == PackageManager.PERMISSION_GRANTED) {
          if (view.isAvailable()) {
            String callEnabledText = view.getContext().getString(R.string.titleCanMakeCall);
            view.showError(callEnabledText);
          }
        }
      }
    }
  }

  private void dispatchAskPermissionsIfNeeded() {
    if (view.isAvailable()) {
      if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
        if (view.shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
          AlertDialog dialog = new AlertDialog.Builder(view.getContext())
              .setCancelable(true)
              .setTitle(R.string.titleMakeCallPermission)
              .setMessage(R.string.titleMakeCallPermissionMessage)
              .setNegativeButton(android.R.string.cancel, (d, w) -> log(String.format(Locale.ENGLISH, "%d is (negative) selected", w)))
              .setPositiveButton(android.R.string.ok, (d, w) -> {
                if (view.isAvailable()) {
                  view.requestPermissionsCompat(new String[] { Manifest.permission.CALL_PHONE }, PERMISSION_ALLOW_CALL);
                }
              })
              .create();
          dialog.show();
        } else {
          view.requestPermissionsCompat(new String[] { Manifest.permission.CALL_PHONE }, PERMISSION_ALLOW_CALL);
        }
      }
    }
  }
}