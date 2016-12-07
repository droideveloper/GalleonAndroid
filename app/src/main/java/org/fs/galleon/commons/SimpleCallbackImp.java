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
package org.fs.galleon.commons;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public final class SimpleCallbackImp extends ItemTouchHelper.SimpleCallback {

  private final SwipeCallback callback;

  private SimpleCallbackImp(int dragDirs, int swipeDirs, SwipeCallback callback) {
    super(dragDirs, swipeDirs);
    this.callback = callback;
  }

  @Override public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
      RecyclerView.ViewHolder target) {
    return false;
  }

  @Override public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    if (callback != null) {
      callback.onSwiped(viewHolder, direction);
    }
  }

  public static SimpleCallbackImp swipeEndHelper(SwipeCallback callback) {
    return new Builder()
        .drag(0)
        .swipe(ItemTouchHelper.END)
        .callback(callback)
        .build();
  }

  public static SimpleCallbackImp swipeStartHelper(SwipeCallback callback) {
    return new Builder()
        .drag(0)
        .swipe(ItemTouchHelper.START)
        .callback(callback)
        .build();
  }

  public static SimpleCallbackImp swipeBothHelper(SwipeCallback callback) {
    return new Builder()
        .drag(0)
        .swipe(ItemTouchHelper.START | ItemTouchHelper.END)
        .callback(callback)
        .build();
  }

  public interface SwipeCallback {
    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction);
  }

  //builder class
  public static class Builder {
    private int drag;
    private int swipe;
    private SwipeCallback callback;

    public Builder () {}
    public Builder drag(int drag) { this.drag = drag; return this; }
    public Builder swipe(int swipe) { this.swipe = swipe; return this; }
    public Builder callback(SwipeCallback callback) { this.callback = callback; return this; }
    public SimpleCallbackImp build() {
      return new SimpleCallbackImp(drag, swipe, callback);
    }
  }
}
