package com.bq.daggerskeleton.sample.hardware;

import android.hardware.camera2.CameraCharacteristics;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CameraState {

   public boolean cameraOpened = false;
   @NonNull public Map<String, CameraCharacteristics> availableCameras = new HashMap<>();
   @Nullable public String selectedCamera = null;
}
