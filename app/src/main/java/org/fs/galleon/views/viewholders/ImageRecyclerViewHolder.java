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

import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import java.io.File;
import org.fs.common.BusManager;
import org.fs.core.AbstractRecyclerViewHolder;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.R;
import org.fs.galleon.entities.ImageEntity;
import org.fs.galleon.events.ImageClearEvent;
import org.fs.galleon.events.ImageSelectionEvent;
import org.fs.util.ViewUtility;
import rx.Subscription;

public class ImageRecyclerViewHolder extends AbstractRecyclerViewHolder<ImageEntity>
    implements View.OnLongClickListener, View.OnClickListener {

  private ImageEntity data;

  private ImageView viewImage;
  private Subscription eventSub;

  public ImageRecyclerViewHolder(View view) {
    super(view);
    viewImage = ViewUtility.findViewById(view, R.id.viewImage);
  }

  @Override public final void onBindView(ImageEntity data) {
    this.data = data;
    //start loading image
    Glide.with(itemView.getContext())
        .load(new File(data.getImageUri().getPath()))
        .fitCenter()
        .crossFade()
        .into(viewImage);
  }

  @Override public boolean onLongClick(View v) {
    if (!v.isSelected()) {
      v.setSelected(true);
      BusManager.send(new ImageSelectionEvent(true, data));
    }
    return true;
  }

  @Override public void onClick(View v) {
    if (v.isSelected()) {
      v.setSelected(false);
      BusManager.send(new ImageSelectionEvent(false, data));
    }
  }

  public final void onAttach() {
    itemView.setOnClickListener(this);
    itemView.setOnLongClickListener(this);
    eventSub = BusManager.add((evt) -> {
      if (evt instanceof ImageClearEvent) {
        if (itemView.isSelected()) {
          itemView.setSelected(false);
        }
      }
    });
  }

  public final void onDetach() {
    itemView.setOnClickListener(null);
    itemView.setOnLongClickListener(null);
    if (eventSub != null) {
      BusManager.remove(eventSub);
      eventSub = null;
    }
  }

  @Override protected String getClassTag() {
    return ImageRecyclerViewHolder.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }
}