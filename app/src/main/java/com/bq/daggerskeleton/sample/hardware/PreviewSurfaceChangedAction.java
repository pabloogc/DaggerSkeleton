package com.bq.daggerskeleton.sample.hardware;

import android.graphics.SurfaceTexture;

import com.bq.daggerskeleton.flux.Action;

public final class PreviewSurfaceChangedAction implements Action {
   public final SurfaceTexture surfaceTexture;
   public final int width;
   public final int height;

   public PreviewSurfaceChangedAction(SurfaceTexture surfaceTexture, int width, int height) {
      this.surfaceTexture = surfaceTexture;
      this.width = width;
      this.height = height;
   }

   @Override public String toString() {
      return "PreviewSurfaceChangedAction{" +
            "width=" + width +
            ", height=" + height +
            '}';
   }
}
