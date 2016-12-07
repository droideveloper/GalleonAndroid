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
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import java.util.List;
import javax.inject.Inject;
import org.fs.core.AbstractActivity;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.R;
import org.fs.galleon.components.ApplicationComponent;
import org.fs.galleon.components.DaggerActivityComponent;
import org.fs.galleon.entities.Contact;
import org.fs.galleon.entities.Document;
import org.fs.galleon.managers.IDatabaseManager;
import org.fs.galleon.modules.ActivityModule;
import org.fs.galleon.presenters.ICustomerDetailActivityPresenter;
import org.fs.galleon.views.adapters.ContactStatePagerAdapter;
import org.fs.galleon.views.adapters.DocumentRecyclerAdapter;
import org.fs.util.ViewUtility;

public class CustomerDetailActivityView extends AbstractActivity<ICustomerDetailActivityPresenter>
    implements ICustomerDetailActivityView {

  @Inject ICustomerDetailActivityPresenter presenter;
  @Inject IDatabaseManager dbManager;

  private Toolbar         viewToolbar;
  private ViewPager       viewPager;
  private RecyclerView    viewRecycler;
  private View            viewProgress;
  private View            viewProgressDelete;

  private ContactStatePagerAdapter contactPagerAdapter;
  private DocumentRecyclerAdapter documentRecyclerAdapter;

  @Override public void onCreate(Bundle restoreState) {
    super.onCreate(restoreState);
    setContentView(R.layout.view_customer_detail_activity);
    DaggerActivityComponent.builder()
        .applicationComponent(getApplicationComponent())
        .activityModule(new ActivityModule(this))
        .build()
        .inject(this);
    presenter.restoreState(restoreState != null ? restoreState : getIntent().getExtras());
    presenter.onCreate();
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    presenter.storeState(outState);
  }

  @Override public void onStart() {
    super.onStart();
    //start observe
    presenter.observeToolbar(viewToolbar);
    presenter.observeRecycler(viewRecycler);
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

  @Override public void showError(String str) {
    Snackbar.make(view(), str, Snackbar.LENGTH_LONG)
        .show();
  }

  @Override public void showError(String str, String buttonText, View.OnClickListener listener) {
    final Snackbar snackbar = Snackbar.make(view(), str, Snackbar.LENGTH_LONG);
    snackbar.setAction(buttonText, (v) -> {
      if (listener != null) {
        listener.onClick(v);
      }
      snackbar.dismiss();
    });
    snackbar.show();
  }

  @Override public void setUpViews() {
    viewToolbar = ViewUtility.findViewById(this, R.id.viewToolbar);
    setSupportActionBar(viewToolbar);
    viewToolbar.setNavigationIcon(R.drawable.ic_back);
    //viewPager
    viewPager = ViewUtility.findViewById(this, R.id.viewPager);
    contactPagerAdapter = new ContactStatePagerAdapter(getSupportFragmentManager());
    viewPager.setAdapter(contactPagerAdapter);
    //recyclerView
    viewRecycler = ViewUtility.findViewById(this, R.id.viewRecycler);
    viewRecycler.setLayoutManager(new LinearLayoutManager(this));
    viewRecycler.setItemAnimator(new DefaultItemAnimator());
    viewRecycler.setHasFixedSize(true);
    documentRecyclerAdapter = new DocumentRecyclerAdapter(getContext(), dbManager);
    viewRecycler.setAdapter(documentRecyclerAdapter);
    //progressView
    viewProgress = ViewUtility.findViewById(this, R.id.viewProgress);
    viewProgressDelete = ViewUtility.findViewById(this, R.id.viewProgressDelete);
  }

  @Override public void setTitleText(String titleStr) {
    viewToolbar.setTitle(titleStr);
    ActionBar supportActionBar = getSupportActionBar();
    if (supportActionBar != null) {
      supportActionBar.setDisplayShowTitleEnabled(true);
      supportActionBar.setTitle(titleStr);
    }
  }

  @Override public void setSubTitleText(String subTitleStr) {
    viewToolbar.setSubtitle(subTitleStr);
  }

  @Override public void bindDocumentAdapter(List<Document> dataSet) {
    if (documentRecyclerAdapter != null) {
      documentRecyclerAdapter.clearIfExists();
      documentRecyclerAdapter.appendData(dataSet, false);
    }
  }

  @Override public void removeDocumentAt(int position) {
    if (documentRecyclerAdapter != null) {
      documentRecyclerAdapter.removeAt(position);
    }
  }

  @Override public void bindContactAdapter(List<Contact> dataSet) {
    if (contactPagerAdapter != null) {
      contactPagerAdapter.clearIfExists();
      contactPagerAdapter.addAll(dataSet, false);
    }
  }

  @Override public void finish() {
    super.finish();
    overridePendingTransition(R.anim.scale_in, R.anim.translate_out_right);
  }

  @Override public void showProgressDelete() {
    viewProgressDelete.setVisibility(View.VISIBLE);
  }

  @Override public void hideProgressDelete() {
    viewProgressDelete.setVisibility(View.GONE);
  }

  @Override public void showProgress() {
    viewProgress.setVisibility(View.VISIBLE);
    viewRecycler.setVisibility(View.GONE);
    viewPager.setVisibility(View.GONE);
  }

  @Override public void hideProgress() {
    viewProgress.setVisibility(View.GONE);
    viewRecycler.setVisibility(View.VISIBLE);
    viewPager.setVisibility(View.VISIBLE);
  }

  @Override public void onBackPressed() {
    presenter.onBackPressed();
  }

  @Override public Context getContext() {
    return this;
  }

  @Override protected String getClassTag() {
    return CustomerDetailActivityView.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }

  private View view() {
    return findViewById(android.R.id.content);
  }

  private ApplicationComponent getApplicationComponent() {
    return ((GalleonApplication) getApplication()).applicationComponent();
  }
}