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
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.io.File;
import javax.inject.Inject;
import org.fs.core.AbstractFragment;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.R;
import org.fs.galleon.components.ApplicationComponent;
import org.fs.galleon.components.DaggerFragmentComponent;
import org.fs.galleon.modules.FragmentModule;
import org.fs.galleon.presenters.IPreviewProcessFragmentPresenter;
import org.fs.galleon.presenters.PreviewProcessFragmentPresenter;

public class PreviewProcessFragmentView extends AbstractFragment<IPreviewProcessFragmentPresenter>
    implements IPreviewProcessFragmentView {

  @Inject IPreviewProcessFragmentPresenter presenter;


  public static PreviewProcessFragmentView newInstance(String absolutePath) {
    PreviewProcessFragmentView fragment = new PreviewProcessFragmentView();
    Bundle args = new Bundle();
    args.putString(PreviewProcessFragmentPresenter.KEY_FILE_PATH, absolutePath);
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater factory, ViewGroup parent, Bundle restoreState) {
    return setUpViews(factory.inflate(R.layout.view_preview_process_fragment, parent, false));
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
    return view;
  }

  @Override public void setPdfFile(File file) {
  }

  @Override public boolean isAvailable() {
    return isCallingSafe();
  }

  @Override protected String getClassTag() {
    return PreviewProcessFragmentView.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }

  private ApplicationComponent getApplicationComponent() {
    GalleonApplication app = (GalleonApplication) getActivity().getApplication();
    return app != null ? app.applicationComponent() : null;
  }
}