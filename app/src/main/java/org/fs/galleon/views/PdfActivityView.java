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
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import javax.inject.Inject;
import org.fs.core.AbstractActivity;
import org.fs.core.AbstractApplication;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.R;
import org.fs.galleon.components.ApplicationComponent;
import org.fs.galleon.components.DaggerActivityComponent;
import org.fs.galleon.modules.ActivityModule;
import org.fs.galleon.presenters.IPdfActivityPresenter;
import org.fs.util.ViewUtility;
import org.fs.viewpdf.PDFView;

public class PdfActivityView extends AbstractActivity<IPdfActivityPresenter>
    implements IPdfActivityView {

  private final static int DEFAULT_PAGE_INDEX = 0;

  private Toolbar viewToolbar;
  private PDFView viewPdf;

  @Inject IPdfActivityPresenter presenter;

  @Override public void onCreate(Bundle restoreState) {
    super.onCreate(restoreState);
    setContentView(R.layout.view_pdf_activity);
    DaggerActivityComponent.builder()
        .applicationComponent(getApplicationComponent())
        .activityModule(new ActivityModule(this))
        .build()
        .inject(this);
    //if its not directly called from that we do it this way
    if (restoreState == null) {
      restoreState = new Bundle();
      Intent intent = getIntent();
      restoreState.putString("bundle.src.file.path", intent.getData().getPath());
    }
    presenter.restoreState(restoreState);
    presenter.onCreate();
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
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

  @Override public void setup() {
    viewToolbar = ViewUtility.findViewById(this, R.id.viewToolbar);
    viewToolbar.setNavigationIcon(R.drawable.ic_back);
    viewToolbar.setNavigationOnClickListener(presenter.provideOnClickListener());

    viewPdf = ViewUtility.findViewById(this, R.id.viewPdfView);
  }

  @Override public void setPdfUri(Uri uri) {
    viewPdf.fromUri(uri)
        .defaultPage(DEFAULT_PAGE_INDEX)
        .enableSwipe(true)
        .enableDoubletap(true)
        .enableAnnotationRendering(false)
        .load();
  }

  @Override public void finish() {
    super.finish();
    overridePendingTransition(R.anim.scale_in, R.anim.translate_out_right);
  }

  @Override public void recycle() {
    viewPdf.recycle();
  }

  @Override public void setPdfTitle(String pdfTitleText) {
    viewToolbar.setTitle(pdfTitleText);
  }

  @Override public void onBackPressed() {
    presenter.backPressed();
  }

  @Override public Context getContext() {
    return this;
  }

  @Override protected String getClassTag() {
    return PdfActivityView.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return AbstractApplication.isDebug();
  }

  private ApplicationComponent getApplicationComponent() {
    GalleonApplication app = (GalleonApplication) getApplication();
    return app != null ? app.applicationComponent() : null;
  }
}