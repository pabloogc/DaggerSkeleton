package com.bq.daggerskeleton.sample.hardware;

import android.hardware.camera2.CameraCaptureSession;

import com.bq.daggerskeleton.flux.Action;

public class CaptureSessionStarted implements Action {
   public final CameraCaptureSession session;

   public CaptureSessionStarted(CameraCaptureSession session) {
      this.session = session;
   }
}
