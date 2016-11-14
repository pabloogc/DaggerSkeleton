package com.bq.daggerskeleton.sample.hardware;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Surface;

import java.util.HashMap;
import java.util.Map;

public class CameraState {

   public boolean canOpenCamera = false;
   public CameraDevice cameraDevice;
   public Surface previewSurface;
   public CameraCaptureSession session;
   public String selectedCamera = null;
   @NonNull public Map<String, CameraCharacteristics> availableCameras = new HashMap<>();

   public CameraState() {
   }

   public CameraState(CameraState other) {
      this.canOpenCamera = other.canOpenCamera;
      this.cameraDevice = other.cameraDevice;
      this.previewSurface = other.previewSurface;
      this.session = other.session;
      this.selectedCamera = other.selectedCamera;
      this.availableCameras = other.availableCameras;
   }

   @Override public String toString() {
      return "CameraState{" +
            "canOpenCamera=" + canOpenCamera +
            ", cameraDevice=" + cameraDevice.getId() +
            ", previewSurface=" + previewSurface +
            ", session=" + session +
            ", selectedCamera='" + selectedCamera + '\'' +
            '}';
   }
}
