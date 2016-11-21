package com.bq.daggerskeleton.sample.hardware;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.support.annotation.NonNull;
import android.util.Size;
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

   //TODO: This belong to another store
   public SurfaceTexture previewTexture;
   public Surface previewSurface;
   public Size previewSize;

   public CameraDevice cameraDevice;

   public CameraState() {
   }

   @SuppressWarnings("IncompleteCopyConstructor")
   public CameraState(CameraState other) {
      this.canOpenCamera = other.canOpenCamera;
      this.selectedCamera = other.selectedCamera;
      this.availableCameras = other.availableCameras;
      this.previewTexture = other.previewTexture;
      this.previewSurface = other.previewSurface;
      this.previewSize = other.previewSize;
      this.cameraDevice = other.cameraDevice;
   }

   @Override public String toString() {
      return "CameraState{" +
            "canOpenCamera=" + canOpenCamera +
            ", selectedCamera='" + selectedCamera + '\'' +
            ", availableCameras=" + availableCameras +
            ", previewTexture=" + previewTexture +
            ", previewSize=" + previewSize +
            ", cameraDevice=" + cameraDevice +
            ", previewSurface=" + previewSurface +
            '}';
   }
}
