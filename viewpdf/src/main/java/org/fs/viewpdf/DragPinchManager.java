/**
 * Copyright 2016 Bartosz Schiller
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fs.viewpdf;

import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import java.lang.ref.WeakReference;
import org.fs.viewpdf.scroll.ScrollHandle;

import static org.fs.viewpdf.util.Constants.Pinch.MAXIMUM_ZOOM;
import static org.fs.viewpdf.util.Constants.Pinch.MINIMUM_ZOOM;

/**
 * This Manager takes care of moving the PDFView,
 * set its zoom track user actions.
 */
class DragPinchManager
    implements GestureDetector.OnGestureListener,
    GestureDetector.OnDoubleTapListener,
    ScaleGestureDetector.OnScaleGestureListener,
    View.OnTouchListener {

  private WeakReference<PDFView> viewCache;

  private AnimationManager animationManager;

  private GestureDetector gestureDetector;
  private ScaleGestureDetector scaleGestureDetector;

  private boolean isSwipeEnabled;

  private boolean swipeVertical;

  private boolean scrolling = false;

  public DragPinchManager(PDFView pdfView, AnimationManager animationManager) {
    this.viewCache = pdfView != null ? new WeakReference<>(pdfView) : null;
    this.animationManager = animationManager;
    this.isSwipeEnabled = false;
    this.swipeVertical = pdfView != null && pdfView.isSwipeVertical();
    if (pdfView != null) {
      gestureDetector = new GestureDetector(pdfView.getContext(), this);
      scaleGestureDetector = new ScaleGestureDetector(pdfView.getContext(), this);
      pdfView.setOnTouchListener(this);
    }
  }

  public void enableDoubletap(boolean enableDoubletap) {
    if (enableDoubletap) {
      gestureDetector.setOnDoubleTapListener(this);
    } else {
      gestureDetector.setOnDoubleTapListener(null);
    }
  }

  public boolean isZooming() {
    final PDFView cached = getCachedView();
    return cached != null && cached.isZooming();
  }

  private boolean isPageChange(float distance) {
    final PDFView cached = getCachedView();
    return cached != null && Math.abs(distance) > Math.abs(cached.toCurrentScale(
        swipeVertical ? cached.getOptimalPageHeight() : cached.getOptimalPageWidth()) / 2);
  }

  public void setSwipeEnabled(boolean isSwipeEnabled) {
    this.isSwipeEnabled = isSwipeEnabled;
  }

  public void setSwipeVertical(boolean swipeVertical) {
    this.swipeVertical = swipeVertical;
  }

  @Override public boolean onSingleTapConfirmed(MotionEvent e) {
    final PDFView cached = getCachedView();
    if (cached != null) {
      ScrollHandle ps = cached.getScrollHandle();
      if (ps != null && !cached.documentFitsView()) {
        if (!ps.shown()) {
          ps.show();
        } else {
          ps.hide();
        }
      }
    }
    return true;
  }

  @Override public boolean onDoubleTap(MotionEvent e) {
    final PDFView cached = getCachedView();
    if (cached != null) {
      if (cached.getZoom() < cached.getMidZoom()) {
        cached.zoomWithAnimation(e.getX(), e.getY(), cached.getMidZoom());
      } else if (cached.getZoom() < cached.getMaxZoom()) {
        cached.zoomWithAnimation(e.getX(), e.getY(), cached.getMaxZoom());
      } else {
        cached.resetZoomWithAnimation();
      }
    }
    return true;
  }

  @Override public boolean onDoubleTapEvent(MotionEvent e) {
    return false;
  }

  @Override public boolean onDown(MotionEvent e) {
    animationManager.stopFling();
    return true;
  }

  @Override public void onShowPress(MotionEvent e) {

  }

  @Override public boolean onSingleTapUp(MotionEvent e) {
    return false;
  }

  @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
    scrolling = true;
    final PDFView cached = getCachedView();
    if (cached != null) {
      if (isZooming() || isSwipeEnabled) {
        cached.moveRelativeTo(-distanceX, -distanceY);
      }
      cached.loadPageByOffset();
    }
    return true;
  }

  public void onScrollEnd(MotionEvent event) {
    final PDFView cached = getCachedView();
    if (cached != null) {
      cached.loadPages();
      hideHandle();
    }
  }

  @Override public void onLongPress(MotionEvent e) {

  }

  @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    final PDFView cached = getCachedView();
    if (cached != null) {
      int xOffset = (int) cached.getCurrentXOffset();
      int yOffset = (int) cached.getCurrentYOffset();
      animationManager.startFlingAnimation(xOffset, yOffset, (int) (velocityX), (int) (velocityY),
          xOffset * (swipeVertical ? 2 : cached.getPageCount()), 0,
          yOffset * (swipeVertical ? cached.getPageCount() : 2), 0);
    }
    return true;
  }

  @Override public boolean onScale(ScaleGestureDetector detector) {
    final PDFView cached = getCachedView();
    if (cached != null) {
      float dr = detector.getScaleFactor();
      float wantedZoom = cached.getZoom() * dr;
      if (wantedZoom < MINIMUM_ZOOM) {
        dr = MINIMUM_ZOOM / cached.getZoom();
      } else if (wantedZoom > MAXIMUM_ZOOM) {
        dr = MAXIMUM_ZOOM / cached.getZoom();
      }
      cached.zoomCenteredRelativeTo(dr, new PointF(detector.getFocusX(), detector.getFocusY()));
    }
    return true;
  }

  @Override public boolean onScaleBegin(ScaleGestureDetector detector) {
    return true;
  }

  @Override public void onScaleEnd(ScaleGestureDetector detector) {
    final PDFView cached = getCachedView();
    if (cached != null) {
      cached.loadPages();
      hideHandle();
    }
  }

  @Override public boolean onTouch(View v, MotionEvent event) {
    boolean retVal = scaleGestureDetector.onTouchEvent(event);
    retVal = gestureDetector.onTouchEvent(event) || retVal;

    if (event.getAction() == MotionEvent.ACTION_UP) {
      if (scrolling) {
        scrolling = false;
        onScrollEnd(event);
      }
    }
    return retVal;
  }

  private void hideHandle() {
    final PDFView cached = getCachedView();
    if (cached != null) {
      if (cached.getScrollHandle() != null && cached.getScrollHandle().shown()) {
        cached.getScrollHandle().hideDelayed();
      }
    }
  }

  private PDFView getCachedView() {
    return viewCache != null ? viewCache.get() : null;
  }
}
