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
package org.fs.galleon.views.viewholders;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.fs.common.BusManager;
import org.fs.core.AbstractRecyclerViewHolder;
import org.fs.exception.AndroidException;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.R;
import org.fs.galleon.entities.Document;
import org.fs.galleon.entities.Syncable;
import org.fs.galleon.events.DocumentEvent;
import org.fs.galleon.managers.IDatabaseManager;
import org.fs.util.InvokeUtility;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static org.fs.util.ViewUtility.findViewById;

public class DocumentRecyclerViewHolder extends AbstractRecyclerViewHolder<Document>
  implements View.OnClickListener{

  private Document data;

  private TextView txtDocumentName;
  private TextView txtLastModified;
  private ImageView imgViewAction;
  private View     viewProgress;
  private Subscription eventSub;

  private final SimpleDateFormat dateFormat;
  private final IDatabaseManager dbManager;

  public DocumentRecyclerViewHolder(View view, IDatabaseManager dbManager) {
    super(view);
    this.dbManager = dbManager;
    this.dateFormat = new SimpleDateFormat("dd MMM yy, HH:mm", Locale.getDefault());
    txtDocumentName = findViewById(view, R.id.txtDocumentName);
    txtLastModified = findViewById(view, R.id.txtLastModified);
    viewProgress = findViewById(view, R.id.viewProgress);
    imgViewAction = findViewById(view, R.id.imgViewAction);
  }

  @Override public final void onBindView(Document data) {
    this.data = data;
    txtDocumentName.setText(data.getDocumentName());
    txtLastModified.setText(dateFormat.format(data.getUpdateDate()));
    imgViewAction.setImageResource(R.drawable.ic_download);
    loadActionImage();
  }

  public Document getDocument() {
    return this.data;
  }

  @Override public void onClick(View v) {
    dbManager.firstOrDefault(data.getDocumentId())
        //.filter(this::exists)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::options, this::log);
  }

  public void onViewAttached() {
    View view = view();
    if (view != null) {
      view.setOnClickListener(this);
    }
    eventSub = BusManager.add((evt) -> {
      if (evt instanceof DocumentEvent) {
        DocumentEvent event = (DocumentEvent) evt;
        if (event.isDownloadStart()
            && event.entity().equals(data)) {
          viewProgress.setVisibility(View.VISIBLE);
        } else if(event.isDownloadFinish()
            && event.entity().equals(data)) {
          viewProgress.setVisibility(View.GONE);
          loadActionImage();//action changes here
        } else if (event.isView()
            && event.entity().equals(data)) {
          invoke(viewFile(event.entity()));
        }
      }
    });
  }

  public void onViewDetached() {
    View view = view();
    if (view != null) {
      view.setOnClickListener(null);
    }
    if (eventSub != null) {
      BusManager.remove(eventSub);
      eventSub = null;
    }
  }

  @Override protected void log(Throwable error) {
    super.log(error);
    throw new AndroidException(error);
  }

  @Override protected String getClassTag() {
    return DocumentRecyclerViewHolder.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }

  private Observable<File> viewFile(Document data) {
    return dbManager.firstOrDefault(data.getDocumentId())
        .map(syncable -> new File(syncable.getLocalPath()));
  }

  private void loadActionImage() {
    dbManager.firstOrDefault(data.getDocumentId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(x -> {
          if (exists(x)) {
            imgViewAction.setImageResource(R.drawable.ic_view);
          }
        }, this::log);
  }

  private void invoke(Observable<File> view) {
    view.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(file -> {
          final String extension = InvokeUtility.invoke(() -> {
            String path = file.getAbsolutePath();
            return path.substring(path.lastIndexOf('.') + 1)
                .toLowerCase(Locale.getDefault());
          });
          log(Log.INFO,
              String.format(Locale.getDefault(), "extension is %s.", extension)
          );
          MimeTypeMap extensionMap = MimeTypeMap.getSingleton();
          String contentType = extensionMap.getMimeTypeFromExtension(extension);
          Intent intent = new Intent(Intent.ACTION_VIEW);
          intent.setDataAndType(Uri.fromFile(file), contentType);
          intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK);
          intent = Intent.createChooser(intent, view().getContext()
              .getString(R.string.titleChooseApplication));
          try {
            view().getContext()
                .startActivity(intent);
          } catch (ActivityNotFoundException notFound) {
            throw new AndroidException(notFound);
          }
        }, this::log);
  }

  private View view() {
    return itemView;
  }

  private boolean exists(Syncable syncable) {
    return syncable != null && new File(syncable.getLocalPath()).exists();
  }

  private void options(Syncable syncable) {
    if (!exists(syncable)) {
      BusManager.send(new DocumentEvent(DocumentEvent.DocumentAction.DOWNLOAD, data));
    } else {
      BusManager.send(new DocumentEvent(DocumentEvent.DocumentAction.VIEW, data));
    }
  }
}