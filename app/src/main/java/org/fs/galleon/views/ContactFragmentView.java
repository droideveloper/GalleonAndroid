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
package org.fs.galleon.views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.lang.ref.WeakReference;
import java.util.Locale;
import javax.inject.Inject;
import org.fs.core.AbstractFragment;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.R;
import org.fs.galleon.components.ApplicationComponent;
import org.fs.galleon.components.DaggerFragmentComponent;
import org.fs.galleon.entities.Contact;
import org.fs.galleon.modules.FragmentModule;
import org.fs.galleon.presenters.ContactFragmentPresenter;
import org.fs.galleon.presenters.IContactFragmentPresenter;
import org.fs.util.PreconditionUtility;
import org.fs.util.ViewUtility;

public class ContactFragmentView extends AbstractFragment<IContactFragmentPresenter>
    implements IContactFragmentView {

  @Inject IContactFragmentPresenter presenter;

  private WeakReference<View> viewReference;
  private TextView txtPhone;
  private TextView txtAddress;
  private TextView txtCityAndCountry;

  public static ContactFragmentView newInstance(Contact contact) {
    PreconditionUtility.checkNotNull(contact, "Contact object instance is null.");
    ContactFragmentView fragment = new ContactFragmentView();
    Bundle args = new Bundle();
    args.putParcelable(ContactFragmentPresenter.KEY_CONTACT, contact);
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater factory, ViewGroup parent, Bundle restoreState) {
    return setUpView(factory.inflate(R.layout.view_contact_fragment, parent, false));
  }

  @Override public void onActivityCreated(Bundle restoreState) {
    super.onActivityCreated(restoreState);
    DaggerFragmentComponent.builder()
        .applicationComponent(getApplicationComponent())
        .fragmentModule(new FragmentModule(this))
        .build()
        .inject(this);
    presenter.restoreState(restoreState != null ? restoreState : getArguments());
    presenter.onCreate();
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    presenter.storeState(outState);
  }

  @Override public void onStart() {
    super.onStart();
    presenter.observeClicks(txtPhone);
    presenter.onStart();
  }

  @Override public void onStop() {
    presenter.onStop();
    super.onStop();
  }

  @Override public void onDestroy() {
    presenter.onDestroy();
    super.onDestroy();
  }

  @Override public FragmentManager getSupportFragmentManager() {
    return getFragmentManager();
  }

  @Override public void hideCityAndCountry() {
    txtCityAndCountry.setVisibility(View.GONE);
  }

  @Override public void showCityAndCountry() {
    txtCityAndCountry.setVisibility(View.VISIBLE);
  }

  @Override public void setPhone(String phoneStr) {
    txtPhone.setText(phoneStr);
  }

  @Override public void setAddress(String addressStr) {
    txtAddress.setText(addressStr);
  }

  @Override public void setCityAndCountry(String cityAndCountryStr) {
    txtCityAndCountry.setText(cityAndCountryStr);
  }

  @Override public View setUpView(View view) {
    viewReference = new WeakReference<>(view);
    txtPhone = ViewUtility.findViewById(view, R.id.txtPhone);
    txtAddress = ViewUtility.findViewById(view, R.id.txtAddress);
    txtCityAndCountry = ViewUtility.findViewById(view, R.id.txtCityAndCountry);
    return view;
  }

  @Override public void showError(String errorStr) {
    final View view = view();
    if (view != null) {
      Snackbar.make(view, errorStr, Snackbar.LENGTH_LONG)
          .show();
    } else {
      log(Log.WARN,
          String.format(Locale.ENGLISH, "Error occured and snackbar is not available, '%s'", errorStr)
      );
    }
  }

  @Override public void showError(String errorStr, String btnStr, View.OnClickListener listener) {
    final View view = view();
    if (view != null) {
      final Snackbar snackbar = Snackbar.make(view, errorStr, Snackbar.LENGTH_LONG);
      snackbar.setAction(btnStr, (v) -> {
        if (listener != null) {
          listener.onClick(v);
        }
        snackbar.dismiss();
      });
      snackbar.show();
    } else {
      log(Log.WARN,
          String.format(Locale.ENGLISH, "Error occured and snackbar is not available, '%s'", errorStr)
      );
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  @Override public void requestPermissionsCompat(String[] permissions, int requestCode) {
    requestPermissions(permissions, requestCode);
  }

  @Override public boolean isAvailable() {
    return isCallingSafe();
  }

  @Override protected String getClassTag() {
    return ContactFragmentView.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }

  private View view() {
    return viewReference != null ? viewReference.get() : null;
  }

  private ApplicationComponent getApplicationComponent() {
    return getApplication().applicationComponent();
  }

  private GalleonApplication getApplication() {
    return (GalleonApplication) getActivity().getApplication();
  }
}