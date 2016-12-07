package org.fs.viewpdf;

import android.graphics.RectF;
import android.util.Pair;
import java.lang.ref.WeakReference;
import org.fs.viewpdf.util.Constants;
import org.fs.viewpdf.util.MathUtils;

import static org.fs.viewpdf.util.Constants.Cache.CACHE_SIZE;

class PagesLoader {

  private WeakReference<PDFView> viewCache;

  // variables set on every call to loadPages()
  private int cacheOrder;
  private float scaledHeight;
  private float scaledWidth;
  private Pair<Integer, Integer> colsRows;
  private float xOffset;
  private float yOffset;
  private float rowHeight;
  private float colWidth;
  private float pageRelativePartWidth;
  private float pageRelativePartHeight;
  private float partRenderWidth;
  private float partRenderHeight;
  private int thumbnailWidth;
  private int thumbnailHeight;
  private final RectF thumbnailRect = new RectF(0, 0, 1, 1);

  private class Holder {
    int page;
    int row;
    int col;
  }

  public PagesLoader(PDFView pdfView) {
    this.viewCache = pdfView != null ? new WeakReference<>(pdfView) : null;
  }

  private Pair<Integer, Integer> getPageColsRows() {
    final PDFView cached = getCachedView();
    if (cached != null) {
      float ratioX = 1f / cached.getOptimalPageWidth();
      float ratioY = 1f / cached.getOptimalPageHeight();
      final float partHeight = (Constants.PART_SIZE * ratioY) / cached.getZoom();
      final float partWidth = (Constants.PART_SIZE * ratioX) / cached.getZoom();
      final int nbRows = MathUtils.ceil(1f / partHeight);
      final int nbCols = MathUtils.ceil(1f / partWidth);
      return new Pair<>(nbCols, nbRows);
    }
    return null;
  }

  private int documentPage(int userPage) {
    final PDFView cached = getCachedView();
    if (cached != null) {
      int documentPage = userPage;
      if (cached.getFilteredUserPages() != null) {
        if (userPage < 0 || userPage >= cached.getFilteredUserPages().length) {
          return -1;
        } else {
          documentPage = cached.getFilteredUserPages()[userPage];
        }
      }

      if (documentPage < 0 || userPage >= cached.getDocumentPageCount()) {
        return -1;
      }

      return documentPage;
    }
    return -1;
  }

  private Holder getPageAndCoordsByOffset(float offset) {
    Holder holder = new Holder();
    float fixOffset = -MathUtils.max(offset, 0);

    final PDFView cached = getCachedView();
    if (cached != null) {
      if (cached.isSwipeVertical()) {
        holder.page = MathUtils.floor(fixOffset / scaledHeight);
        holder.row = MathUtils.floor(Math.abs(fixOffset - scaledHeight * holder.page) / rowHeight);
        holder.col = MathUtils.floor(xOffset / colWidth);
      } else {
        holder.page = MathUtils.floor(fixOffset / scaledWidth);
        holder.col = MathUtils.floor(Math.abs(fixOffset - scaledWidth * holder.page) / colWidth);
        holder.row = MathUtils.floor(yOffset / rowHeight);
      }
    }
    return holder;
  }

  private void loadThumbnail(int userPage, int documentPage) {
    final PDFView cached = getCachedView();
    if (cached != null) {
      if (!cached.cacheManager.containsThumbnail(userPage, documentPage, thumbnailWidth,
          thumbnailHeight, thumbnailRect)) {
        cached.renderingAsyncTask.addRenderingTask(userPage, documentPage, thumbnailWidth,
            thumbnailHeight, thumbnailRect, true, 0, cached.isBestQuality(), cached.isAnnotationRendering());
      }
    }
  }

  /**
   * @param number if < 0 then row (column) is above view, else row (column) is visible or below
   * view
   */
  private int loadRelative(int number, int nbOfPartsLoadable, boolean outsideView) {
    int loaded = 0;
    float newOffset;
    final PDFView cached = getCachedView();
    if (cached != null) {
      if (cached.isSwipeVertical()) {
        float rowsHeight = rowHeight * number + 1;
        newOffset = cached.getCurrentYOffset() - (outsideView ? cached.getHeight() : 0) - rowsHeight;
      } else {
        float colsWidth = colWidth * number;
        newOffset = cached.getCurrentXOffset() - (outsideView ? cached.getWidth() : 0) - colsWidth;
      }

      Holder holder = getPageAndCoordsByOffset(newOffset);
      int documentPage = documentPage(holder.page);
      if (documentPage < 0) {
        return 0;
      }
      loadThumbnail(holder.page, documentPage);

      if (cached.isSwipeVertical()) {
        int firstCol = MathUtils.floor(xOffset / colWidth);
        firstCol = MathUtils.min(firstCol - 1, 0);
        int lastCol = MathUtils.ceil((xOffset + cached.getWidth()) / colWidth);
        lastCol = MathUtils.max(lastCol + 1, colsRows.first);
        for (int col = firstCol; col <= lastCol; col++) {
          if (loadCell(holder.page, documentPage, holder.row, col, pageRelativePartWidth,
              pageRelativePartHeight)) {
            loaded++;
          }
          if (loaded >= nbOfPartsLoadable) {
            return loaded;
          }
        }
      } else {
        int firstRow = MathUtils.floor(yOffset / rowHeight);
        firstRow = MathUtils.min(firstRow - 1, 0);
        int lastRow = MathUtils.ceil((yOffset + cached.getHeight()) / rowHeight);
        lastRow = MathUtils.max(lastRow + 1, colsRows.second);
        for (int row = firstRow; row <= lastRow; row++) {
          if (loadCell(holder.page, documentPage, row, holder.col, pageRelativePartWidth,
              pageRelativePartHeight)) {
            loaded++;
          }
          if (loaded >= nbOfPartsLoadable) {
            return loaded;
          }
        }
      }
    }
    return loaded;
  }

  public int loadVisible() {
    int parts = 0;
    Holder firstHolder, lastHolder;
    final PDFView cached = getCachedView();
    if (cached != null) {
      if (cached.isSwipeVertical()) {
        firstHolder = getPageAndCoordsByOffset(cached.getCurrentYOffset());
        lastHolder = getPageAndCoordsByOffset(cached.getCurrentYOffset() - cached.getHeight() + 1);
        int visibleRows = 0;
        if (firstHolder.page == lastHolder.page) {
          visibleRows = lastHolder.row - firstHolder.row + 1;
        } else {
          visibleRows += colsRows.second - firstHolder.row;
          for (int page = firstHolder.page + 1; page < lastHolder.page; page++) {
            visibleRows += colsRows.second;
          }
          visibleRows += lastHolder.row + 1;
        }

        for (int i = 0; i < visibleRows && parts < CACHE_SIZE; i++) {
          parts += loadRelative(i, CACHE_SIZE - parts, false);
        }
      } else {
        firstHolder = getPageAndCoordsByOffset(cached.getCurrentXOffset());
        lastHolder = getPageAndCoordsByOffset(cached.getCurrentXOffset() - cached.getWidth() + 1);
        int visibleCols = 0;
        if (firstHolder.page == lastHolder.page) {
          visibleCols = lastHolder.col - firstHolder.col + 1;
        } else {
          visibleCols += colsRows.first - firstHolder.col;
          for (int page = firstHolder.page + 1; page < lastHolder.page; page++) {
            visibleCols += colsRows.first;
          }
          visibleCols += lastHolder.col + 1;
        }

        for (int i = 0; i < visibleCols && parts < CACHE_SIZE; i++) {
          parts += loadRelative(i, CACHE_SIZE - parts, false);
        }
      }
      int prevDocPage = documentPage(firstHolder.page - 1);
      if (prevDocPage >= 0) {
        loadThumbnail(firstHolder.page - 1, prevDocPage);
      }
      int nextDocPage = documentPage(firstHolder.page + 1);
      if (nextDocPage >= 0) {
        loadThumbnail(firstHolder.page + 1, nextDocPage);
      }
    }
    return parts;
  }

  private boolean loadCell(int userPage, int documentPage, int row, int col, float pageRelativePartWidth, float pageRelativePartHeight) {

    float relX = pageRelativePartWidth * col;
    float relY = pageRelativePartHeight * row;
    float relWidth = pageRelativePartWidth;
    float relHeight = pageRelativePartHeight;

    // Adjust width and height to
    // avoid being outside the page
    float renderWidth = partRenderWidth;
    float renderHeight = partRenderHeight;
    if (relX + relWidth > 1) {
      relWidth = 1 - relX;
    }
    if (relY + relHeight > 1) {
      relHeight = 1 - relY;
    }
    renderWidth *= relWidth;
    renderHeight *= relHeight;
    RectF pageRelativeBounds = new RectF(relX, relY, relX + relWidth, relY + relHeight);

    if (renderWidth > 0 && renderHeight > 0) {
      final PDFView cached = getCachedView();
      if (cached != null) {
        if (!cached.cacheManager.upPartIfContained(userPage, documentPage, renderWidth, renderHeight,
            pageRelativeBounds, cacheOrder)) {
          cached.renderingAsyncTask.addRenderingTask(userPage, documentPage, renderWidth,
              renderHeight, pageRelativeBounds, false, cacheOrder, cached.isBestQuality(), cached.isAnnotationRendering());
        }
      }

      cacheOrder++;
      return true;
    }
    return false;
  }

  public void loadPages() {
    final PDFView cached = getCachedView();
    if (cached != null) {
      scaledHeight = cached.toCurrentScale(cached.getOptimalPageHeight());
      scaledWidth = cached.toCurrentScale(cached.getOptimalPageWidth());
      thumbnailWidth = (int) (cached.getOptimalPageWidth() * Constants.THUMBNAIL_RATIO);
      thumbnailHeight = (int) (cached.getOptimalPageHeight() * Constants.THUMBNAIL_RATIO);
      colsRows = getPageColsRows();
      xOffset = -MathUtils.max(cached.getCurrentXOffset(), 0);
      yOffset = -MathUtils.max(cached.getCurrentYOffset(), 0);
      rowHeight = scaledHeight / colsRows.second;
      colWidth = scaledWidth / colsRows.first;
      pageRelativePartWidth = 1f / (float) colsRows.first;
      pageRelativePartHeight = 1f / (float) colsRows.second;
      partRenderWidth = Constants.PART_SIZE / pageRelativePartWidth;
      partRenderHeight = Constants.PART_SIZE / pageRelativePartHeight;
      cacheOrder = 1;
      int loaded = loadVisible();
      if (cached.getScrollDir().equals(PDFView.ScrollDir.END)) { // if scrolling to end, preload next view
        for (int i = 0; i < Constants.PRELOAD_COUNT && loaded < CACHE_SIZE; i++) {
          loaded += loadRelative(i, loaded, true);
        }
      } else { // if scrolling to start, preload previous view
        for (int i = 0; i > -Constants.PRELOAD_COUNT && loaded < CACHE_SIZE; i--) {
          loaded += loadRelative(i, loaded, false);
        }
      }
    }
  }

  private PDFView getCachedView() {
    return viewCache != null ? viewCache.get() : null;
  }
}