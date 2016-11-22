package com.bq.daggerskeleton.sample.hardware;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.bq.daggerskeleton.flux.Dispatcher;
import com.bq.daggerskeleton.flux.Store;
import com.bq.daggerskeleton.sample.app.App;
import com.bq.daggerskeleton.sample.app.AppScope;

import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import timber.log.Timber;


/**
 * Store that controls {@link CameraDevice} lifecycle.
 */
@AppScope
public class CameraStore extends Store<CameraState> {

   private static final int CAMERA_LOCK_TIMEOUT = 3000; //3s

   private final App app;
   private final CameraManager cameraManager;

   private final Handler backgroundHandler;
   private final Semaphore cameraLock = new Semaphore(1);

   @Inject CameraStore(App app, Handler backgroundHandler) {
      this.app = app;
      this.cameraManager = ((CameraManager) app.getSystemService(Context.CAMERA_SERVICE));
      this.backgroundHandler = backgroundHandler;

      Dispatcher.subscribe(CameraPermissionChanged.class, permissionChanged -> {
         CameraState newState = new CameraState(state());
         newState.canOpenCamera = permissionChanged.granted;
         setState(newState);
      });

      Dispatcher.subscribe(OpenCameraAction.class, a -> {
         setState(tryToOpenCamera(state()));
      });

      Dispatcher.subscribe(CloseCameraAction.class, a -> setState(closeCamera(state())));

      Dispatcher.subscribe(CameraOpenedAction.class, a -> {
         CameraState newState = new CameraState(state());
         newState.cameraDevice = a.camera;
         setState(newState);
      });
   }

   private CameraState tryToOpenCamera(CameraState state) {
      if (state().cameraDevice != null) return state;
      if (!state().canOpenCamera) return state;

      CameraState newState = new CameraState(state);

      try {
         if (!cameraLock.tryAcquire(CAMERA_LOCK_TIMEOUT, TimeUnit.MILLISECONDS)) {
            // STOPSHIP: 16/11/2016
            // TODO: 16/11/2016 Move to an error status and notify user properly
            throw new IllegalStateException("Failed to open camera in time");
         }
         Timber.v("Opening camera");

         populateCameraMap(newState.availableCameras);
         newState.selectedCamera = selectDefaultCamera(newState.availableCameras);
         try {
            //noinspection MissingPermission
            cameraManager.openCamera(newState.selectedCamera, new CameraDevice.StateCallback() {
               @Override
               public void onOpened(@NonNull CameraDevice camera) {
                  cameraLock.release();
                  Dispatcher.dispatchOnUi(new CameraOpenedAction(camera));
               }

               @Override
               public void onDisconnected(@NonNull CameraDevice camera) {
                  Timber.d("Camera disconnected: %s", camera.getId());
               }

               @Override
               public void onError(@NonNull CameraDevice camera, int error) {
                  cameraLock.release();
                  Timber.e("Camera error: %d", error);
               }
            }, backgroundHandler);
         } catch (CameraAccessException e) {
            Timber.e(e);
         }
      } catch (InterruptedException e) {
         e.printStackTrace();
      }

      return newState;
   }

   private CameraState closeCamera(CameraState state) {
      CameraState newState = new CameraState(state);
      try {
         if (!cameraLock.tryAcquire(CAMERA_LOCK_TIMEOUT, TimeUnit.MILLISECONDS)) {
            // STOPSHIP: 16/11/2016
            // TODO: 16/11/2016 Move to an error status and notify user properly
            throw new IllegalStateException("Failed to close camera in time");
         }

         if (newState.cameraDevice != null) {
            newState.cameraDevice.close();
            newState.cameraDevice = null;
         }

      } catch (InterruptedException e) {
         e.printStackTrace(); //Should never happen
      } finally {
         cameraLock.release();
      }
      return newState;
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

   @NonNull
   private String selectDefaultCamera(Map<String, CameraCharacteristics> cameras) {
      for (String cameraId : cameras.keySet()) {
         CameraCharacteristics cameraCharacteristics = cameras.get(cameraId);
         if (!CameraCharacteristicsUtil.isFrontCamera(cameraCharacteristics)) {
            return cameraId;
         }
      }
      return cameras.keySet().iterator().next();
   }

   @Module
   public static class CameraModule {
      @Provides @AppScope @IntoMap @ClassKey(CameraStore.class)
      static Store<?> provideCameraStoreToMap(CameraStore store) {
         return store;
      }
   }
}
