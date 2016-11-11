package com.bq.daggerskeleton.sample.hardware;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;

import com.bq.daggerskeleton.flux.Dispatcher;
import com.bq.daggerskeleton.flux.Store;
import com.bq.daggerskeleton.sample.app.App;
import com.bq.daggerskeleton.sample.app.AppScope;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;


@AppScope
public class CameraStore extends Store<CameraState> {

   private final App app;
   private final CameraManager cameraManager;

   @Inject CameraStore(App app) {
      this.app = app;
      cameraManager = ((CameraManager) app.getSystemService(Context.CAMERA_SERVICE));

      Dispatcher.subscribe(CameraPermissionChanged.class, permissionChanged -> {
         if (permissionChanged.granted) {
            CameraState newState = new CameraState();
            newState.availableCameras = new HashMap<>();
            populateCameraMap(newState.availableCameras);
            setState(newState);
         }
      });
   }

   @Override protected CameraState initialState() {
      return new CameraState();
   }

   @Module
   public static class CameraModule {
      @Provides @AppScope @IntoSet
      static Store<?> provideCameraStoreToSet(CameraStore store) {
         return store;
      }
   }

   private void populateCameraMap(Map<String, CameraCharacteristics> cameraMap) {
      try {
         for (String cameraId : cameraManager.getCameraIdList()) {
            cameraMap.put(cameraId, cameraManager.getCameraCharacteristics(cameraId));
         }
      } catch (CameraAccessException e) {
         e.printStackTrace(); //Permission denied, should not happen
      }
   }
}
