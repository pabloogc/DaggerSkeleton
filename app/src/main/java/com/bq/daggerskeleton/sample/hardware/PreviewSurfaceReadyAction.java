package com.bq.daggerskeleton.sample.hardware;

import android.graphics.SurfaceTexture;

import com.bq.daggerskeleton.flux.Action;

public final class PreviewSurfaceReadyAction implements Action {
   public final SurfaceTexture surfaceTexture;
   public final int width;
   public final int height;

   public PreviewSurfaceReadyAction(SurfaceTexture surfaceTexture, int width, int height) {
      this.surfaceTexture = surfaceTexture;
      this.width = width;
      this.height = height;
   }

   @Override public String toString() {
      return "PreviewSurfaceReadyAction{" +
            "width=" + width +
            ", height=" + height +
            '}';
   }
}
