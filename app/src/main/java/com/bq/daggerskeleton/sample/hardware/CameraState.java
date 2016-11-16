package com.bq.daggerskeleton.sample.hardware;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.support.annotation.NonNull;
import android.util.Size;
import android.view.Surface;

import java.util.HashMap;
import java.util.Map;

public class CameraState {

   public boolean canOpenCamera = false;
   public String selectedCamera = null;
   @NonNull public Map<String, CameraCharacteristics> availableCameras = new HashMap<>();

   public SurfaceTexture previewTexture;
   public Surface previewSurface;
   public Size previewSize;

   public CameraDevice cameraDevice;
   public CameraCaptureSession session;

   public CameraState() {
   }

   public CameraState(CameraState other) {
      this.canOpenCamera = other.canOpenCamera;
      this.selectedCamera = other.selectedCamera;
      this.availableCameras = other.availableCameras;
      this.previewTexture = other.previewTexture;
      this.previewSize = other.previewSize;
      this.cameraDevice = other.cameraDevice;
      this.session = other.session;
      this.previewSurface = other.previewSurface;
   }

   @Override public String toString() {
      return "CameraState{" +
            "canOpenCamera=" + canOpenCamera +
            ", selectedCamera='" + selectedCamera + '\'' +
            ", availableCameras=" + availableCameras +
            ", previewTexture=" + previewTexture +
            ", previewSize=" + previewSize +
            ", cameraDevice=" + cameraDevice +
            ", session=" + session +
            ", previewSurface=" + previewSurface +
            '}';
   }
}
