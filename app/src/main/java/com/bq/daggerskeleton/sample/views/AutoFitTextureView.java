package com.bq.daggerskeleton.sample.views;

/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

/**
 * A {@link TextureView} that can be adjusted to a specified aspect ratio.
 */
public class AutoFitTextureView extends TextureView {

   public static final float RATIO_STANDARD = 4f / 3f;
   public static final float RATIO_FULL_SCREEN = 16f / 9f;
   public static final float RATIO_ONE_ONE = 1f;
   private Matrix matrix = null;
   private float aspectRatio = RATIO_FULL_SCREEN;
   private float aspectRatioScale = 1f;
   private boolean aspectRatioResize;
   private boolean isOneToOne;

   private int previewWidth = 0;
   private int mPreviewHeight = 0;
   /**
    * Whenever a layout change is detected, we need to apply the new transformation
    */
   private OnLayoutChangeListener onLayoutChangeListener = new OnLayoutChangeListener() {
      @Override
      public void onLayoutChange(View v, int left, int top, int right,
                                 int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

         int width = right - left;
         int height = bottom - top;

         if (previewWidth != width || mPreviewHeight != height
               || (aspectRatioResize)) {
            previewWidth = width;
            mPreviewHeight = height;
            setTransformMatrix(width, height);
            aspectRatioResize = false;
         }
      }
   };

   public AutoFitTextureView(Context context) {
      this(context, null);
      init();
   }

   public AutoFitTextureView(Context context, AttributeSet attrs) {
      this(context, attrs, 0);
      init();
   }

   public AutoFitTextureView(Context context, AttributeSet attrs, int defStyle) {
      super(context, attrs, defStyle);
      init();
   }

   //Add layout change listener
   public void init() {
      this.addOnLayoutChangeListener(onLayoutChangeListener);
   }

   /**
    * Calculate the new texture sizes depending on the orientation. Right now the camera only works
    * with vertical orientation, so we only need to scale the height.
    *
    * @param widthMeasureSpec  Horizontal space requirements as imposed by the parent. The requirements are encoded with View.MeasureSpec.
    * @param heightMeasureSpec Vertical space requirements as imposed by the parent. The requirements are encoded with View.MeasureSpec.
    */
   @Override
   protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
      int width = MeasureSpec.getSize(widthMeasureSpec);
      int height = MeasureSpec.getSize(heightMeasureSpec);

      int rotation = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getRotation();
      boolean isInHorizontal = Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation;

      int newWidth;
      int newHeight;

      if (isInHorizontal) {
         newHeight = getMeasuredHeight();
         if (isOneToOne) newWidth = getMeasuredHeight();
         else newWidth = (int) (newHeight * aspectRatio);
      } else {
         newWidth = getMeasuredWidth();
         if (isOneToOne) newHeight = getMeasuredWidth();
         else newHeight = (int) (newWidth * aspectRatio);
      }

      setMeasuredDimension(newWidth, newHeight);
   }

   private void setTransformMatrix(int width, int height) {
      matrix = getTransform(matrix);
      float scaleX, scaleY;
      float scaledTextureWidth, scaledTextureHeight;

      scaledTextureWidth = Math.max(width * aspectRatioScale,
            (int) (height / aspectRatio));
      scaledTextureHeight = Math.max(height,
            (int) (width * aspectRatio));

      scaleX = scaledTextureWidth / width;
      scaleY = scaledTextureHeight / height;

      float px;
      float py;

      //Pivot from the center
      px = (float) width / 2;
      py = (float) height / 2;

      matrix.setScale(scaleX, scaleY, px, py);
      setTransform(matrix);
   }

   /**
    * Set the texture aspect ratio. Depending on the switch type (bigger or smaller) we need a
    * different scale to apply in the transformation matrix.
    *
    * @param aspectRatio
    */
   public void setAspectRatio(float aspectRatio) {
      //If the ratio is 1:1, we should use the 4:3 ratio to crop the preview
      if (aspectRatio == RATIO_ONE_ONE) {
         aspectRatio = RATIO_STANDARD;
         isOneToOne = true;
         aspectRatioScale = 1f; //Reset scale to avoid 16:9 modifications
      } else {
         isOneToOne = false;
      }

      if (this.aspectRatio != aspectRatio) {
         aspectRatioResize = true;
         this.aspectRatio = aspectRatio;
         requestLayout();
      }
   }

   /**
    * Reset the aspect ratio to 4:3 to avoid deformation when the camera is closed since
    * we are setting a 4:3 default ratio for the texture buffer.
    */
   public void resetAspectRatio() {
      aspectRatio = RATIO_STANDARD;
   }

   public Size getPreviewSize() {
      return new Size(previewWidth, mPreviewHeight);
   }

   public interface OnTextureViewChangeListener {

      /**
       * Called when the onLayoutChange is invoked.
       *
       * @param previewWidth  New preview width.
       * @param previewHeight New preview heigth.
       */
      void onPreviewSizeChanged(int previewWidth, int previewHeight);
   }
}
