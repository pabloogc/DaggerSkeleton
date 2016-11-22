package com.bq.daggerskeleton.sample.preview;

import android.graphics.SurfaceTexture;

import com.bq.daggerskeleton.flux.Action;

@SuppressWarnings("javadoctype")
public final class PreviewSurfaceReadyAction implements Action {
   public final SurfaceTexture surfaceTexture;

   public PreviewSurfaceReadyAction(SurfaceTexture surfaceTexture) {
      this.surfaceTexture = surfaceTexture;
   }

   @Override public String toString() {
      return "PreviewSurfaceReadyAction{" +
            "surfaceTexture=" + surfaceTexture +
            '}';
   }
}
