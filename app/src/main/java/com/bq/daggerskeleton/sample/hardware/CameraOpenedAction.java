package com.bq.daggerskeleton.sample.hardware;

import com.bq.daggerskeleton.flux.Action;

import durdinapps.rxcamera2.RxCameraDevice;

public class CameraOpenedAction implements Action {
   public final RxCameraDevice camera;

   public CameraOpenedAction(RxCameraDevice camera) {
      this.camera = camera;
   }

   @Override public String toString() {
      return "CameraOpenedAction{" +
            "camera=" + camera.getCameraDevice().getId() +
            '}';
   }
}
