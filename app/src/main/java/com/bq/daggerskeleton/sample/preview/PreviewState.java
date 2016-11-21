package com.bq.daggerskeleton.sample.preview;

import android.graphics.SurfaceTexture;
import android.util.Size;
import android.view.Surface;

public class PreviewState {

   public SurfaceTexture previewTexture;
   public Surface previewSurface;
   public Size previewSize;

   public PreviewState() {
   }

   public PreviewState(PreviewState other) {
      this.previewTexture = other.previewTexture;
      this.previewSurface = other.previewSurface;
      this.previewSize = other.previewSize;
   }

   @Override public String toString() {
      return "PreviewState{" +
            "previewTexture=" + previewTexture +
            ", previewSurface=" + previewSurface +
            ", previewSize=" + previewSize +
            '}';
   }
}
