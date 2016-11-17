package com.bq.daggerskeleton.sample.preview;

import android.util.Size;

import com.bq.daggerskeleton.flux.Action;

public final class PreviewSurfaceBufferCalculatedAction implements Action {
   public final Size size;

   public PreviewSurfaceBufferCalculatedAction(Size size) {
      this.size = size;
   }

   @Override public String toString() {
      return "PreviewSurfaceBufferCalculatedAction{" +
            "size=" + size +
            '}';
   }
}
