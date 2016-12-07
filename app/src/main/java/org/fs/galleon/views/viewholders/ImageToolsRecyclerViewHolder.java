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
import android.widget.TextView;
import com.bumptech.glide.Glide;
import org.fs.common.BusManager;
import org.fs.core.AbstractApplication;
import org.fs.core.AbstractRecyclerViewHolder;
import org.fs.galleon.R;
import org.fs.galleon.entities.ImageEntity;
import org.fs.galleon.events.SingleImageSelectionEvent;

import static org.fs.util.ViewUtility.findViewById;

public class ImageToolsRecyclerViewHolder extends AbstractRecyclerViewHolder<ImageEntity>
    implements View.OnClickListener {

  private ImageEntity data;
  private TextView txtImageName;
  private ImageView imgViewThumbnail;

  public ImageToolsRecyclerViewHolder(View view) {
    super(view);
    view.setOnClickListener(this);
    txtImageName = findViewById(view, R.id.txtImageName);
    imgViewThumbnail = findViewById(view, R.id.imgViewThumbnail);
  }

  @Override protected String getClassTag() {
    return ImageToolsRecyclerViewHolder.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return AbstractApplication.isDebug();
  }

  @Override public final void onBindView(ImageEntity data) {
    this.data = data;
    txtImageName.setText(toImageName());
    Glide.with(itemView.getContext())
        .load(data.getImageUri())
        .into(imgViewThumbnail);
  }

  public void setSelected(boolean isSelected) {
    itemView.setSelected(isSelected);
  }

  @Override public void onClick(View v) {
    BusManager.send(new SingleImageSelectionEvent(data, getAdapterPosition()));
  }

  private String toImageName() {
    String path = data.getImageUri().getPath();
    return path.substring(path.lastIndexOf("/") + 1);
  }
}