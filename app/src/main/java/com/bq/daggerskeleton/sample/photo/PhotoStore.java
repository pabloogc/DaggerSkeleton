package com.bq.daggerskeleton.sample.photo;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Size;

import com.bq.daggerskeleton.flux.Dispatcher;
import com.bq.daggerskeleton.flux.Store;
import com.bq.daggerskeleton.sample.app.AppScope;
import com.bq.daggerskeleton.sample.hardware.CameraCharacteristicsUtil;
import com.bq.daggerskeleton.sample.hardware.CameraOpenedAction;
import com.bq.daggerskeleton.sample.hardware.CameraStore;
import com.bq.daggerskeleton.sample.hardware.CloseCameraAction;
import com.bq.daggerskeleton.sample.hardware.session.OutputSurfaceReadyAction;
import com.bq.daggerskeleton.sample.hardware.session.SessionState;
import com.bq.daggerskeleton.sample.hardware.session.SessionStore;
import com.bq.daggerskeleton.sample.rotation.RotationState;
import com.bq.daggerskeleton.sample.rotation.RotationStore;
import com.bq.daggerskeleton.sample.rotation.RotationUtils;

import java.nio.ByteBuffer;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import timber.log.Timber;

@AppScope
public class PhotoStore extends Store<PhotoState> {

   private final CameraStore cameraStore;
   private final RotationStore rotationStore;
   private final SessionStore sessionStore;
   private final Handler backgroundHandler;

   @Inject PhotoStore(CameraStore cameraStore,
                      RotationStore rotationStore,
                      SessionStore sessionStore,
                      Handler backgroundHandler) {

      this.cameraStore = cameraStore;
      this.rotationStore = rotationStore;
      this.sessionStore = sessionStore;
      this.backgroundHandler = backgroundHandler;

      // Subscribe to CameraOpened action to create an ImageReader
      Dispatcher.subscribe(CameraOpenedAction.class, action -> {
         if (isInPhotoMode()) {
            setState(createImageReader());
            Dispatcher.dispatch(new OutputSurfaceReadyAction(state().imageReader.getSurface()));
         }
      });

      // Release the imageReader when closing the camera, as we'll nee to configure it again sometime
      Dispatcher.subscribe(CloseCameraAction.class, action -> {
         if (isInPhotoMode()) {
            setState(releaseImageReader());
         }
      });
   }

   @Override
   protected PhotoState initialState() {
      return new PhotoState();
   }

   private boolean isInPhotoMode() {
      return SessionState.OutputMode.PHOTO == sessionStore.state().outputMode;
   }

   @NonNull
   private PhotoState createImageReader() {
      String currentCameraId = cameraStore.state().selectedCamera;
      CameraCharacteristics cameraCharacteristics = cameraStore.state().availableCameras.get(currentCameraId);
      // TODO: 21/11/16 Configure resolution via Settings
      Size resolution = CameraCharacteristicsUtil.getJpegOutputSizes(cameraCharacteristics).get(0);

      ImageReader imageReader = ImageReader.newInstance(
            resolution.getWidth(),
            resolution.getHeight(),
            ImageFormat.JPEG,
            2);

      imageReader.setOnImageAvailableListener(reader -> {
         try (Image image = reader.acquireLatestImage()) {
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            Dispatcher.dispatch(new PhotoBytesCapturedAction(bytes));
         }
      }, null);

      PhotoState newState = new PhotoState(state());
      newState.imageReader = imageReader;
      return newState;
   }

   private void takePicture() {
      CameraCharacteristics cameraCharacteristics = cameraStore.state().availableCameras.get(
            cameraStore.state().selectedCamera);

      final CaptureRequest captureRequest = setupJpegRequest(
            cameraStore.state().cameraDevice,
            cameraCharacteristics,
            rotationStore.state());

      if (captureRequest != null) {
         try {
            sessionStore.state().session.capture(captureRequest, new CameraCaptureSession.CaptureCallback() {
               @Override
               public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                  super.onCaptureCompleted(session, request, result);
                  Dispatcher.dispatch(new PhotoCaptureCompletedAction(result));
               }

               @Override
               public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
                  super.onCaptureFailed(session, request, failure);
                  Timber.e(
                        "Error capturing photo!\nReason %s in frame %d, was image captured? -> %s",
                        failure.getReason(),
                        failure.getFrameNumber(),
                        failure.wasImageCaptured());
               }
            }, this.backgroundHandler);
         } catch (CameraAccessException e) {
            e.printStackTrace();
            // TODO: 21/11/16 We should notify about an error while taking the picture
         }

      } else {
         // TODO: 21/11/16 We should notify about an error while taking the picture
      }
   }

   private CaptureRequest setupJpegRequest(CameraDevice cameraDevice,
                                           CameraCharacteristics cameraCharacteristics,
                                           RotationState rotationState) {
      try {
         // Create capture request for taking photos
         CaptureRequest.Builder captureRequestBuilder =
               cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);

         // TODO: 21/11/16 Move configuration params to another store
         // AutoFocus and AutoExposure
         captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_AUTO);
         captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_ANTIBANDING_MODE_AUTO);
         // Get rotation from state
         captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION,
               RotationUtils.getSensorDeviceOrientationCompensation(
                     CameraCharacteristicsUtil.isFrontCamera(cameraCharacteristics),
                     rotationState.deviceAbsoluteRotation,
                     CameraCharacteristicsUtil.getSensorOrientation(cameraCharacteristics)));
         // Full quality
         captureRequestBuilder.set(CaptureRequest.JPEG_QUALITY, (byte) 100);

         return captureRequestBuilder.build();
      } catch (CameraAccessException e) {
         Timber.e("Could not create capture request");
         return null;
      }
   }

   private PhotoState releaseImageReader() {
      if (state().imageReader != null) {
         state().imageReader.getSurface().release();
         state().imageReader.close();
      }
      return initialState();
   }

   @Module
   public static class PhotoModule {
      @Provides @AppScope @IntoMap @ClassKey(PhotoStore.class)
      static Store<?> providePhotoStoreToMap(PhotoStore store) {
         return store;
      }
   }
}
