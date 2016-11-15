package com.bq.daggerskeleton.sample.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.TextureView;

import timber.log.Timber;

/**
 * A {@link TextureView} that can be adjusted to a specified aspect ratio.
 * <p>
 * From Google Camera Sample.
 */
public class PreviewTextureView extends TextureView {

   private boolean fillScreen;
   private float ratio;

   public PreviewTextureView(Context context) {
      super(context);
   }

   public PreviewTextureView(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   /**
    * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
    * calculated from the parameters. Note that the actual sizes of parameters don't matter, that
    * is, calling setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.
    *
    * @param width  Relative horizontal size
    * @param height Relative vertical size
    */
   public void setAspectRatio(int width, int height, boolean fillScreen) {
      if (width < 0 || height < 0) {
         throw new IllegalArgumentException("Size cannot be negative.");
      }
      this.fillScreen = fillScreen;
      this.ratio = width / (float) height;
      Timber.d("Setting preview aspect ratio to %dx%d - %f", width, height, ratio);
      requestLayout();
   }


   @Override
   protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
      int width = MeasureSpec.getSize(widthMeasureSpec);
      int height = MeasureSpec.getSize(heightMeasureSpec);

      float containerRatio = width / (float) height;
      float targetWidth = 0;
      float targetHeight = 0;

      if (ratio == 0) {
         setMeasuredDimension(width, height);
         return;
      }

      if (fillScreen) {
      } else {
         targetHeight = width;
         targetWidth = width * ratio;
      }

      setMeasuredDimension((int) targetWidth, (int) targetHeight);

   }
}