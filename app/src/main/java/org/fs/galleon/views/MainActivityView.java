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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import javax.inject.Inject;
import org.fs.core.AbstractActivity;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.R;
import org.fs.galleon.components.ApplicationComponent;
import org.fs.galleon.components.DaggerActivityComponent;
import org.fs.galleon.modules.ActivityModule;
import org.fs.galleon.presenters.IMainActivityPresenter;
import org.fs.util.ViewUtility;

public class MainActivityView extends AbstractActivity<IMainActivityPresenter>
    implements IMainActivityView {

  private DrawerLayout      viewDrawerLayout;
  private NavigationView    viewNavigation;
  private Toolbar           viewToolbar;
  private View              viewProgress;

  private boolean visible;
  private Menu menu;

  @Inject IMainActivityPresenter presenter;

  @Override public void onCreate(Bundle restoreState) {
    super.onCreate(restoreState);
    setContentView(R.layout.view_main_activity);
    //inject it here
    DaggerActivityComponent.builder()
        .activityModule(new ActivityModule(this))
        .applicationComponent(getApplicationComponent())
        .build()
        .inject(this);

    presenter.handleSearch(getIntent());
    presenter.restoreState(restoreState != null ? restoreState : getIntent().getExtras());
    presenter.onCreate();
  }

  @Override protected void onNewIntent(Intent intent) {
    presenter.handleSearch(intent);
  }

  @Override protected void onSaveInstanceState(Bundle storeState) {
    super.onSaveInstanceState(storeState);
    presenter.storeState(storeState);
  }

  @Override public void onStart() {
    super.onStart();
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

  @Override public boolean isSelected(@IdRes int id) {
    return viewNavigation.getMenu().findItem(id).isChecked();
  }

  @Override public void setUpViews() {
    viewToolbar = ViewUtility.findViewById(this, R.id.viewToolbar);
    setSupportActionBar(viewToolbar);
    ActionBar actionBar  = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setDisplayShowTitleEnabled(true);
    }
    viewToolbar.setNavigationIcon(R.drawable.ic_menu);

    viewDrawerLayout = ViewUtility.findViewById(this, R.id.viewDrawerLayout);
    viewNavigation = ViewUtility.findViewById(this, R.id.viewNavigation);
    viewProgress = ViewUtility.findViewById(this, R.id.viewProgress);

    viewNavigation.setNavigationItemSelectedListener(presenter.provideNavigationListener());
  }

  @Override public void setTitleText(String titleStr) {
    viewToolbar.setTitle(titleStr);
  }

  @Override public void setSelected(@IdRes int id) {
    viewNavigation.setCheckedItem(id);
  }

  @Override public <T extends Fragment> void replaceAndCommit(T view) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
        android.R.anim.fade_in, android.R.anim.fade_out);
    transaction.replace(R.id.viewContent, view);
    transaction.commit();
  }

  @Override public boolean isMenuOpen() {
    return viewDrawerLayout.isDrawerOpen(GravityCompat.START);
  }

  @Override public void closeMenu() {
    if (isMenuOpen()) {
      viewDrawerLayout.closeDrawer(GravityCompat.START);
    }
  }

  @Override public void openMenu() {
    if (!isMenuOpen()) {
      viewDrawerLayout.openDrawer(GravityCompat.START);
    }
  }

  @Override public void showError(String errorStr) {
    Snackbar.make(view(), errorStr, Snackbar.LENGTH_LONG)
        .show();
  }

  @Override public void showError(String errorStr, String btnText, View.OnClickListener listener) {
    final Snackbar snackbar = Snackbar.make(view(), errorStr, Snackbar.LENGTH_LONG);
    snackbar.setAction(btnText, (v) -> {
      if (listener != null) {
        listener.onClick(v);
      }
      snackbar.dismiss();
    });
    snackbar.show();
  }

  @Override public boolean isSearchVisible() {
    //no opt
    return false;
  }

  @Override public void showSearch() {
    //no opt
  }

  @Override public void hideSearch() {
    //no opt
  }

  @Override public void showProgress() {
    if (viewProgress != null) {
      viewProgress.setVisibility(View.VISIBLE);
    }
  }

  @Override public void hideProgress() {
    if (viewProgress != null) {
      viewProgress.setVisibility(View.GONE);
    }
  }

  @Override public boolean isProgressVisible() {
    return viewProgress != null && viewProgress.getVisibility() == View.VISIBLE;
  }

  @Override public void startActivity(Intent intent) {
    super.startActivity(intent);
    overridePendingTransition(R.anim.translate_in_right, R.anim.scale_out);
  }

  @Override public void startActivityForResult(Intent intent, int requestCode) {
    super.startActivityForResult(intent, requestCode);
    overridePendingTransition(R.anim.translate_in_right, R.anim.scale_out);
  }

  @Override public void onBackPressed() {
    presenter.onBackPressed();
  }

  @Override public void finish() {
    super.finish();
    overridePendingTransition(R.anim.scale_in, R.anim.translate_out_right);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    return presenter.hasMenuItemSelection(item) || super.onOptionsItemSelected(item);
  }

  @Override public ApplicationComponent getApplicationComponent() {
    return getGalleonApplication().applicationComponent();
  }

  @Override public Context getContext() {
    return this;
  }

  @Override protected String getClassTag() {
    return MainActivityView.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }

  private View view() {
    return findViewById(android.R.id.content);
  }

  private GalleonApplication getGalleonApplication() {
    return (GalleonApplication) getApplication();
  }
}