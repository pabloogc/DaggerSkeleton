package com.bq.daggerskeleton.sample.hardware;

import android.hardware.camera2.CameraDevice;

import com.bq.daggerskeleton.flux.Action;

public class CameraOpenedAction implements Action {
   public final CameraDevice camera;

   public CameraOpenedAction(CameraDevice camera) {
      this.camera = camera;
   }

   @Override public String toString() {
      return "CameraOpenedAction{" +
            "camera=" + camera +
            '}';
   }
}
