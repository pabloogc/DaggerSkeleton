package com.bq.daggerskeleton.sample.hardware;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.Surface;

import com.bq.daggerskeleton.flux.Dispatcher;
import com.bq.daggerskeleton.flux.Store;
import com.bq.daggerskeleton.sample.app.App;
import com.bq.daggerskeleton.sample.app.AppScope;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import timber.log.Timber;


@AppScope
public class CameraStore extends Store<CameraState> {

   private final App app;
   private final CameraManager cameraManager;
   private final Handler backgroundHandler;

   @Override protected CameraState initialState() {
      return new CameraState();
   }

   @Inject CameraStore(App app, Handler backgroundHandler) {
      this.app = app;
      this.cameraManager = ((CameraManager) app.getSystemService(Context.CAMERA_SERVICE));
      this.backgroundHandler = backgroundHandler;

      Dispatcher.subscribe(CameraPermissionChanged.class, permissionChanged -> {
         CameraState newState = new CameraState();
         if (permissionChanged.granted) {
            populateCameraMap(newState.availableCameras);
            newState.selectedCamera = selectDefaultCamera(newState.availableCameras);

            //noinspection MissingPermission
            cameraManager.openCamera(newState.selectedCamera, new CameraDevice.StateCallback() {
               @Override public void onOpened(@NonNull CameraDevice camera) {
                  Dispatcher.dispatch(new CameraOpenedAction(camera));
               }

               @Override public void onDisconnected(@NonNull CameraDevice camera) {

               }

               @Override public void onError(@NonNull CameraDevice camera, int error) {

               }
            }, backgroundHandler);
         }
         setState(newState);
      });

      Dispatcher.subscribe(PreviewSurfaceChangedAction.class, a -> {
         if (state().previewSurface != null) state().previewSurface.release();

         CameraState newState = new CameraState(state());
         a.surfaceTexture.setDefaultBufferSize(a.width, a.height);
         newState.previewSurface = new Surface(a.surfaceTexture);
         setState(newState);

         tryToStartSession();
      });

      Dispatcher.subscribe(CameraOpenedAction.class, a -> {
         CameraState newState = new CameraState(state());
         newState.cameraDevice = a.camera;
         setState(newState);

         tryToStartSession();
      });


      Dispatcher.subscribe(CaptureSessionStarted.class, a -> {
         CameraState newState = new CameraState(state());
         newState.session = a.session;
         setState(newState);
      });
   }

   @Module
   public static class CameraModule {
      @Provides @AppScope @IntoSet
      static Store<?> provideCameraStoreToSet(CameraStore store) {
         return store;
      }
   }

   /**
    * Start the session if preconditions are met. For this example, the preview surface is ready and
    * the camera opened
    */
   private void tryToStartSession() {
      //Preconditions
      if (state().session != null) return; //Already opened
      if (state().cameraDevice == null) return;
      if (state().previewSurface == null) return;

      try {
         CaptureRequest.Builder request = state().cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
         request.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AE_ANTIBANDING_MODE_AUTO);
         request.addTarget(state().previewSurface);

         state().cameraDevice
               .createCaptureSession(Collections.singletonList(state().previewSurface), new CameraCaptureSession.StateCallback() {
                  @Override public void onConfigured(@NonNull CameraCaptureSession session) {
                     Dispatcher.dispatch(new CaptureSessionStarted(session));
                     try {
                        session.setRepeatingRequest(request.build(), null, backgroundHandler);
                     } catch (CameraAccessException e) {
                        Timber.e(e);
                     }
                  }

                  @Override public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                     Timber.e("Failed to create session. %s", state());

                  }
               }, null);

      } catch (CameraAccessException e) {
         Timber.e(e);
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

   @NonNull private String selectDefaultCamera(Map<String, CameraCharacteristics> cameras) {
      for (String cameraId : cameras.keySet()) {
         CameraCharacteristics characteristics = cameras.get(cameraId);
         Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
         if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
            return cameraId;
         }
      }
      return cameras.keySet().iterator().next();
   }
}
