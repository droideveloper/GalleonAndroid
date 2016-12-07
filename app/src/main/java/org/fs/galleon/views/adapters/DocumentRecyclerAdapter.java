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
package org.fs.galleon.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import org.fs.common.BusManager;
import org.fs.core.AbstractRecyclerAdapter;
import org.fs.exception.AndroidException;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.R;
import org.fs.galleon.entities.Document;
import org.fs.galleon.events.DocumentEvent;
import org.fs.galleon.managers.IDatabaseManager;
import org.fs.galleon.views.viewholders.DocumentRecyclerViewHolder;
import org.fs.util.Collections;

public class DocumentRecyclerAdapter
    extends AbstractRecyclerAdapter<Document, DocumentRecyclerViewHolder> {

  private final IDatabaseManager dbManager;

  public DocumentRecyclerAdapter(Context context, IDatabaseManager dbManager) {
    this(new ArrayList<>(), context, dbManager);
  }

  public DocumentRecyclerAdapter(List<Document> dataSet, Context context, IDatabaseManager dbManager) {
    super(dataSet, context);
    this.dbManager = dbManager;
  }

  @Override public void onViewAttachedToWindow(DocumentRecyclerViewHolder viewHolder) {
    super.onViewAttachedToWindow(viewHolder);
    viewHolder.onViewAttached();
  }

  @Override public void onViewDetachedFromWindow(DocumentRecyclerViewHolder viewHolder) {
    super.onViewDetachedFromWindow(viewHolder);
    viewHolder.onViewDetached();
  }

  @Override public DocumentRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater factory = inflaterFactory();
    if (factory != null) {
      final View view = factory.inflate(R.layout.view_document, parent, false);
      return new DocumentRecyclerViewHolder(view, dbManager);
    }
    throw new AndroidException("we can not create instance of LayoutInflater");
  }

  @Override public void onBindViewHolder(DocumentRecyclerViewHolder holder, int position) {
    Document document = getItemAtIndex(position);
    if (document != null) {
      holder.onBindView(document);
    }
  }

  public void clearIfExists() {
    if (!Collections.isNullOrEmpty(dataSet)) {
      dataSet.clear();
      notifyDataSetChanged();
    }
  }

  public void removeAt(int index) {
    if (index >= 0 && index < getItemCount()) {
      Document docx = dataSet.remove(index);
      notifyItemRemoved(index);
      BusManager.send(new DocumentEvent(DocumentEvent.DocumentAction.DELETE, docx));
    }
  }

  @Override public int getItemViewType(int position) {
    return 0;
  }

  @Override protected String getClassTag() {
    return DocumentRecyclerAdapter.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }
}