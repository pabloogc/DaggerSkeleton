package com.bq.daggerskeleton.sample.hardware;

import android.graphics.SurfaceTexture;

import com.bq.daggerskeleton.flux.Action;

public final class PreviewSurfaceReadyAction implements Action {
   public final SurfaceTexture surfaceTexture;
   public final int width;
   public final int height;

   public PreviewSurfaceReadyAction(SurfaceTexture surfaceTexture,
                                    int previewSurfaceWidth,
                                    int previewSurfaceHeight) {
      this.surfaceTexture = surfaceTexture;
      this.width = previewSurfaceWidth;
      this.height = previewSurfaceHeight;
   }

   @Override public String toString() {
      return "PreviewSurfaceReadyAction{" +
            "width=" + width +
            ", height=" + height +
            '}';
   }
}
