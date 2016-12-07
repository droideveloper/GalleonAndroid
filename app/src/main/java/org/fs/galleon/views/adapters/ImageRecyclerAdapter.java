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
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import org.fs.core.AbstractRecyclerAdapter;
import org.fs.core.AbstractRecyclerViewHolder;
import org.fs.exception.AndroidException;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.R;
import org.fs.galleon.entities.ImageEntity;
import org.fs.galleon.views.viewholders.ImageRecyclerViewHolder;
import org.fs.galleon.views.viewholders.LoadingRecyclerViewHolder;
import org.fs.util.Collections;

public class ImageRecyclerAdapter
    extends AbstractRecyclerAdapter<ImageEntity, AbstractRecyclerViewHolder<? extends ImageEntity>> {

  private final static int VIEW_TYPE_ENTITY = 0x01;
  private final static int VIEW_TYPE_LOADER = 0x02;

  public final static int MAX_SPAN  = 3;
  public final static int ITEM_SPAN = 1;

  public ImageRecyclerAdapter(Context context) {
    this(new ArrayList<>(), context);
  }

  public ImageRecyclerAdapter(List<ImageEntity> dataSet, Context context) {
    super(dataSet, context);
  }

  @Override public void onViewDetachedFromWindow(AbstractRecyclerViewHolder<? extends ImageEntity> holder) {
    super.onViewDetachedFromWindow(holder);
    if (holder instanceof ImageRecyclerViewHolder) {
      ImageRecyclerViewHolder viewHolder = (ImageRecyclerViewHolder) holder;
      viewHolder.onDetach();
    }
  }

  @Override public void onViewAttachedToWindow(AbstractRecyclerViewHolder<? extends ImageEntity> holder) {
    super.onViewAttachedToWindow(holder);
    if (holder instanceof ImageRecyclerViewHolder) {
      ImageRecyclerViewHolder viewHolder = (ImageRecyclerViewHolder) holder;
      viewHolder.onAttach();
    }
  }

  @Override public AbstractRecyclerViewHolder<? extends ImageEntity> onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater factory = inflaterFactory();
    if (factory != null) {
      if (viewType == VIEW_TYPE_ENTITY) {
        View view = factory.inflate(R.layout.view_image, parent, false);
        return new ImageRecyclerViewHolder(view);
      } else {
        View view = factory.inflate(R.layout.view_loading, parent, false);
        return new LoadingRecyclerViewHolder(view);
      }
    }
    throw new AndroidException("we can not create LayoutInflater instance");
  }

  @Override public void onBindViewHolder(AbstractRecyclerViewHolder<? extends ImageEntity> holder, int position) {
    ImageEntity entity = getItemAtIndex(position);
    if (entity != null) {
      if (holder instanceof ImageRecyclerViewHolder) {
        ImageRecyclerViewHolder viewHolder = (ImageRecyclerViewHolder) holder;
        viewHolder.onBindView(entity);
      }
    }
  }

  public final GridLayoutManager.SpanSizeLookup provideSpanSizeLookup() {
    return new GridLayoutManager.SpanSizeLookup() {
      @Override public int getSpanSize(int position) {
        if (getItemViewType(position) == VIEW_TYPE_ENTITY) {
          return ITEM_SPAN;
        }
        return MAX_SPAN;
      }
    };
  }

  public final void showLoader() {
    if (dataSet != null) {
      dataSet.add(null);
      notifyItemInserted(dataSet.size() - 1);
    }
  }

  public final void hideLoader() {
    if (!Collections.isNullOrEmpty(dataSet)) {
      int index = dataSet.size() - 1;
      dataSet.remove(index);
      notifyItemRemoved(index);
    }
  }

  @Override public int getItemViewType(int position) {
    return position == getItemCount() - 1 ? VIEW_TYPE_LOADER : VIEW_TYPE_ENTITY;
  }

  @Override protected String getClassTag() {
    return ImageRecyclerAdapter.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }
}