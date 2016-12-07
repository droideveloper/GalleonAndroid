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
import android.support.v7.widget.RecyclerView;
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
import org.fs.galleon.entities.ImageEntity;
import org.fs.galleon.events.SingleImageSelectionEvent;
import org.fs.galleon.views.viewholders.ImageToolsRecyclerViewHolder;
import org.fs.util.Collections;
import rx.Subscription;

public class ImageToolsRecyclerAdapter
    extends AbstractRecyclerAdapter<ImageEntity, ImageToolsRecyclerViewHolder> {

  private Subscription eventSub;
  private int selectedIndex = -1;

  public ImageToolsRecyclerAdapter(Context context) {
    this(new ArrayList<>(), context);
  }

  public ImageToolsRecyclerAdapter(List<ImageEntity> dataSet, Context context) {
    super(dataSet, context);
  }

  @Override public void onAttachedToRecyclerView(RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
    eventSub = BusManager.add((evt) -> {
      if (evt instanceof SingleImageSelectionEvent) {
        SingleImageSelectionEvent event = (SingleImageSelectionEvent) evt;
        this.selectedIndex = event.adapterPosition();
      }
    });
  }

  @Override public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
    super.onDetachedFromRecyclerView(recyclerView);
    if (eventSub != null) {
      BusManager.remove(eventSub);
      eventSub = null;
    }
  }

  @Override public ImageToolsRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater factory = inflaterFactory();
    if (factory != null) {
      View view = factory.inflate(R.layout.view_image_process, parent, false);
      return new ImageToolsRecyclerViewHolder(view);
    }
    throw new AndroidException("we can not create new instance of LayoutInflater class");
  }

  @Override public void onBindViewHolder(ImageToolsRecyclerViewHolder holder, int position) {
    ImageEntity entity = getItemAtIndex(position);
    if (entity != null) {
      holder.onBindView(entity);
    }
    if (position == selectedIndex) {
      holder.setSelected(true);
    } else {
      holder.setSelected(false);
    }
  }

  public final ImageEntity removeAt(int position) {
    if (position >= 0 && position < getItemCount()) {
      ImageEntity entity = dataSet.remove(position);
      //if we removed it
      if (position == selectedIndex) {
        selectedIndex = -1;
      }
      notifyItemRemoved(position);
      return entity;
    }
    return null;
  }

  public void addAt(int position, ImageEntity entity) {
    if (position >= 0 && position < getItemCount()) {
      dataSet.add(position, entity);
      //if we deleted and set back we stay at -1
      if (position == selectedIndex) {
        selectedIndex = selectedIndex + 1;
      }
      notifyItemInserted(position);
    } else {
      //because we are in size...
      if (position == getItemCount()) {
        appendData(entity, false);
      }
    }
  }

  public void clearAll() {
    if (!Collections.isNullOrEmpty(dataSet)) {
      dataSet.clear();
      notifyDataSetChanged();
    }
  }

  @Override public int getItemViewType(int position) {
    return 0;
  }

  @Override protected String getClassTag() {
    return ImageToolsRecyclerAdapter.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }
}