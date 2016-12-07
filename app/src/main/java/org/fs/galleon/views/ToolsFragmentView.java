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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.lang.ref.WeakReference;
import java.util.List;
import javax.inject.Inject;
import org.fs.core.AbstractFragment;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.R;
import org.fs.galleon.components.ApplicationComponent;
import org.fs.galleon.components.DaggerFragmentComponent;
import org.fs.galleon.entities.ImageEntity;
import org.fs.galleon.modules.FragmentModule;
import org.fs.galleon.presenters.IToolsFragmentPresenter;
import org.fs.galleon.views.adapters.ImageToolsRecyclerAdapter;
import org.fs.viewpdf.PDFView;

import static org.fs.util.ViewUtility.findViewById;

public class ToolsFragmentView extends AbstractFragment<IToolsFragmentPresenter>
    implements IToolsFragmentView {

  private static final int DEFAULT_PAGE_INDEX = 0;

  @Inject IToolsFragmentPresenter presenter;

  private WeakReference<View> viewReference;

  private RecyclerView viewRecycler;
  private TextView     txtFileName;
  private PDFView      viewPdf;
  private View         viewProgress;
  private FloatingActionButton viewProcess;

  private ImageToolsRecyclerAdapter imageAdapter;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);//for it to have menu
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater factory, ViewGroup parent, Bundle restoreState) {
    return setUpViews(factory.inflate(R.layout.view_tools_fragment, parent, false));
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    presenter.storeState(outState);
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

  @Override public void onStart() {
    super.onStart();
    presenter.observeRecyclerView(viewRecycler);
    presenter.observeProcessView(viewProcess);
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

  @Override public View setUpViews(View view) {
    viewReference = new WeakReference<>(view);
    viewPdf = findViewById(view, R.id.viewPdf);
    txtFileName = findViewById(view, R.id.txtFileName);

    viewRecycler = findViewById(view, R.id.viewRecycler);
    viewRecycler.setItemAnimator(new DefaultItemAnimator());
    viewRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    viewRecycler.setHasFixedSize(true);
    imageAdapter = new ImageToolsRecyclerAdapter(getContext());
    viewRecycler.setAdapter(imageAdapter);

    viewProgress = findViewById(view, R.id.viewProgress);
    viewProcess = findViewById(view, R.id.buttonProcess);
    return view;
  }

  @Override public void bindRecyclerAdapter(List<ImageEntity> dataSet) {
    if (imageAdapter != null) {
      imageAdapter.appendData(dataSet, false);
    }
  }

  @Override public ImageEntity removeAt(int position) {
    if (imageAdapter != null) {
      return imageAdapter.removeAt(position);
    }
    return null;
  }

  @Override public void addAt(int position, ImageEntity entity) {
    if (imageAdapter != null) {
      imageAdapter.addAt(position, entity);
    }
  }

  @Override public void add(ImageEntity entity) {
    if (imageAdapter != null) {
      imageAdapter.appendData(entity, false);
    }
  }

  @Override public void clearAllAdapters() {
    if (imageAdapter != null) {
      imageAdapter.clearAll();
    }
  }

  @Override public void setPdfFile(Uri uri) {
    viewPdf.fromUri(uri)
        .defaultPage(DEFAULT_PAGE_INDEX)
        .enableSwipe(true)
        .enableDoubletap(true)
        .enableAnnotationRendering(false)
        .load();
  }

  @Override public void setFileTitle(String fileName) {
    txtFileName.setText(fileName);
  }

  @Override public void unloadPdfFile() {
    if (!viewPdf.isRecycled()) {
      viewPdf.recycle();
    }
  }

  @Override public void showError(String errorStr) {
    View view = view();
    if (view != null) {
      Snackbar.make(view, errorStr, Snackbar.LENGTH_LONG)
          .show();
    }
  }

  @Override public void showError(String errorStr, String buttonText, View.OnClickListener listener) {
    View view = view();
    if (view != null) {
      final Snackbar snackbar = Snackbar.make(view, errorStr, Snackbar.LENGTH_LONG);
      snackbar.setAction(buttonText, (v) -> {
        if (listener != null) {
          listener.onClick(v);
        }
        snackbar.dismiss();
      });
      snackbar.show();
    }
  }

  @Override public void requestPermissionsCompat(String[] permissions, int requestCode) {
    requestPermissions(permissions, requestCode);
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    presenter.requestPermissions(requestCode, permissions, grantResults);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    presenter.activityResult(requestCode, resultCode, data);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    return presenter.optionsMenuSelected(item) || super.onOptionsItemSelected(item);
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.tools_menu, menu);
  }

  @Override public void showProgress() {
    viewProgress.setVisibility(View.VISIBLE);
  }

  @Override public void hideProgress() {
    viewProgress.setVisibility(View.GONE);
  }

  @Override public boolean isAvailable() {
    return isCallingSafe();
  }

  @Override protected String getClassTag() {
    return ToolsFragmentView.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }

  @Nullable private View view() {
    return viewReference != null ? viewReference.get() : null;
  }

  private ApplicationComponent getApplicationComponent() {
    GalleonApplication app = (GalleonApplication) getActivity().getApplication();
    return app != null ? app.applicationComponent() : null;
  }
}