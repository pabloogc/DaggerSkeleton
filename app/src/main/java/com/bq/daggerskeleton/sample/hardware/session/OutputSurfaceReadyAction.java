package com.bq.daggerskeleton.sample.hardware.session;

import android.view.Surface;

import com.bq.daggerskeleton.flux.Action;

public class OutputSurfaceReadyAction implements Action {
   public final Surface outputSurface;

   public OutputSurfaceReadyAction(Surface outputSurface) {
      this.outputSurface = outputSurface;
   }

   @Override
   public String toString() {
      return "CaptureTargetSurfaceReadyAction{" +
            "outputSurface=" + outputSurface +
            '}';
   }
}
