package com.bq.daggerskeleton.sample.hardware.session;

/**
 * Created by pablo on 17/11/2016.
 */

public interface CameraDeviceWrapper {

   void openCamera();

   class RealCameraDevice implements CameraDeviceWrapper {


      @Override public void openCamera() {
         //cameraManager.openCamera();
      }
   }
}
