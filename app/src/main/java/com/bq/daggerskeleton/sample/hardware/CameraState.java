package com.bq.daggerskeleton.sample.hardware;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.support.annotation.NonNull;
import android.view.Surface;

import java.util.HashMap;
import java.util.Map;

/**
 * CameraDevice state wrapper.
 */
public class CameraState {

   public boolean canOpenCamera = false;
   public String selectedCamera = null;
   @NonNull public Map<String, CameraCharacteristics> availableCameras = new HashMap<>();

   public Surface targetSurface;

   public CameraDevice cameraDevice;

   public CameraState() {
   }

   public CameraState(CameraState other) {
      this.canOpenCamera = other.canOpenCamera;
      this.selectedCamera = other.selectedCamera;
      this.availableCameras = other.availableCameras;
      this.targetSurface = other.targetSurface;
      this.cameraDevice = other.cameraDevice;
   }

   @Override public String toString() {
      return "CameraState{" +
            "canOpenCamera=" + canOpenCamera +
            ", selectedCamera='" + selectedCamera + '\'' +
            ", availableCameras=" + availableCameras +
            ", outputSurface=" + targetSurface +
            ", cameraDevice=" + cameraDevice +
            '}';
   }
}
