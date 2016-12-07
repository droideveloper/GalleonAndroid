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

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import java.lang.ref.WeakReference;
import java.util.List;
import javax.inject.Inject;
import org.fs.core.AbstractFragment;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.R;
import org.fs.galleon.components.ApplicationComponent;
import org.fs.galleon.components.DaggerFragmentComponent;
import org.fs.galleon.entities.Customer;
import org.fs.galleon.modules.FragmentModule;
import org.fs.galleon.presenters.ISearchFragmentPresenter;
import org.fs.galleon.views.adapters.CustomerRecyclerAdapter;
import org.fs.util.ViewUtility;

public class SearchFragmentView extends AbstractFragment<ISearchFragmentPresenter>
    implements ISearchFragmentView {

  @Inject ISearchFragmentPresenter presenter;

  private WeakReference<View> viewReference;

  private RecyclerView viewRecycler;
  private View         viewProgress;
  private CustomerRecyclerAdapter customerAdapter;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater factory, ViewGroup parent, Bundle restoreState) {
    return setUpViews(factory.inflate(R.layout.view_search_customer_fragment, parent, false));
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
    final View view = view();
    if (view != null) {
      Snackbar.make(view, str, Snackbar.LENGTH_LONG)
          .show();
    } else {
      log(Log.ERROR, str);
    }
  }

  @Override public void showError(String str, String buttonText, View.OnClickListener listener) {
    final View view = view();
    if (view != null) {
      final Snackbar snackbar = Snackbar.make(view, str, Snackbar.LENGTH_LONG);
      snackbar.setAction(buttonText, (v) -> {
        if (listener != null) {
          listener.onClick(v);
        }
        snackbar.dismiss();
      });
      snackbar.show();
    } else {
      log(Log.ERROR, str);
    }
  }

  @Override public void showProgress() {
    //viewProgress.setVisibility(View.VISIBLE);
    //viewRecycler.setVisibility(View.GONE);
  }

  @Override public void hideProgress() {
    //viewProgress.setVisibility(View.GONE);
    //viewRecycler.setVisibility(View.VISIBLE);
  }

  @Override public View setUpViews(View view) {
    viewReference = new WeakReference<>(view);
    viewProgress = ViewUtility.findViewById(view, R.id.viewProgress);
    viewRecycler = ViewUtility.findViewById(view, R.id.viewRecycler);
    //set up other required parts of recycler view
    viewRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    viewRecycler.setItemAnimator(new DefaultItemAnimator());
    viewRecycler.setHasFixedSize(true);
    customerAdapter = new CustomerRecyclerAdapter(getContext());
    viewRecycler.setAdapter(customerAdapter);
    return view;
  }

  @Override public void bindAdapter(List<Customer> dataSet) {
    if (customerAdapter != null) {
      if (customerAdapter.requiresClear()) {
        customerAdapter.clearAll();
      }
      customerAdapter.appendData(dataSet, false);
    }
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.search_options, menu);
    //only way of doing this is this
    Context context = getContext();
    SearchManager searchManager = (SearchManager) context.getSystemService(Context.SEARCH_SERVICE);
    SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
    searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
  }

  @Override public boolean isAvailable() {
    return isCallingSafe();
  }

  @Override protected String getClassTag() {
    return SearchFragmentView.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }

  private View view() {
    return viewReference != null ? viewReference.get() : null;
  }

  private ApplicationComponent getApplicationComponent() {
    return ((GalleonApplication) getActivity().getApplication()).applicationComponent();
  }
}