package com.bq.daggerskeleton.sample.photo;

import android.annotation.SuppressLint;
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
import android.view.Surface;

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

/**
 * Store that handles the resources associated with taking a mediaType (ImageReader) and its lifecycle.
 */
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

      // Create an ImageReader when the camera device is opened
      Dispatcher.subscribe(CameraOpenedAction.class, action -> {
         if (isInPhotoMode()) {
            setState(createImageReader(state()));
            Dispatcher.dispatch(new OutputSurfaceReadyAction(state().imageReader.getSurface()));
         }
      });

      Dispatcher.subscribe(TakePhotoAction.class, action -> {
         takePicture();
      });

      Dispatcher.subscribe(PhotoStatusChangedAction.class, action -> {
         PhotoState newState = new PhotoState(state());
         newState.status = action.status;

         setState(newState);
      });

      // Release the imageReader when closing the camera, as we'll nee to configure it again sometime
      Dispatcher.subscribe(CloseCameraAction.class, action -> {
         if (isInPhotoMode()) {
            setState(releaseImageReader());
         }
      });
   }

   private boolean isInPhotoMode() {
      return SessionState.OutputMode.PHOTO == sessionStore.state().outputMode;
   }

   @NonNull
   private PhotoState createImageReader(PhotoState state) {
      PhotoState newState = new PhotoState(state);

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

            Dispatcher.dispatchOnUi(new PhotoAvailableAction(bytes));
         }
      }, this.backgroundHandler);


      newState.imageReader = imageReader;
      return newState;
   }

   @SuppressLint("DefaultLocale")
   private void takePicture() {
      if (PhotoState.Status.TAKING == state().status) return;

      CameraCharacteristics cameraCharacteristics = cameraStore.state().availableCameras.get(
            cameraStore.state().selectedCamera);

      final CaptureRequest captureRequest = setupJpegRequest(
            cameraStore.state().cameraDevice,
            cameraCharacteristics,
            state().imageReader.getSurface(),
            rotationStore.state());

      if (captureRequest != null) {
         try {
            // Set status to: taking mediaType
            Dispatcher.dispatch(new PhotoStatusChangedAction(PhotoState.Status.TAKING));

            sessionStore.state().session.capture(captureRequest, new CameraCaptureSession.CaptureCallback() {
               @Override
               public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                  super.onCaptureCompleted(session, request, result);

                  Dispatcher.dispatchOnUi(new PhotoStatusChangedAction(PhotoState.Status.SUCCESS));
                  Dispatcher.dispatchOnUi(new PhotoStatusChangedAction(PhotoState.Status.IDLE));
               }

               @Override
               public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
                  super.onCaptureFailed(session, request, failure);
                  Exception captureFailedException = new RuntimeException(
                        String.format("Capture failed: Reason %s in frame %d, was image captured? -> %s",
                              failure.getReason(),
                              failure.getFrameNumber(),
                              failure.wasImageCaptured()));
                  Timber.e(captureFailedException, "Cannot take mediaType, capture failed!");

                  Dispatcher.dispatchOnUi(new PhotoStatusChangedAction(PhotoState.Status.ERROR, captureFailedException));
                  Dispatcher.dispatchOnUi(new PhotoStatusChangedAction(PhotoState.Status.IDLE));
               }
            }, this.backgroundHandler);
         } catch (CameraAccessException | IllegalStateException | IllegalArgumentException | SecurityException e) {
            Timber.e(e, "Cannot take mediaType, capture error!");

            Dispatcher.dispatch(new PhotoStatusChangedAction(PhotoState.Status.ERROR, e));
            Dispatcher.dispatch(new PhotoStatusChangedAction(PhotoState.Status.IDLE));
         }

      } else {
         Timber.e("Cannot take mediaType, captureRequest is null!");

         Dispatcher.dispatch(new PhotoStatusChangedAction(PhotoState.Status.ERROR));
         Dispatcher.dispatch(new PhotoStatusChangedAction(PhotoState.Status.IDLE));
      }
   }

   private CaptureRequest setupJpegRequest(CameraDevice cameraDevice,
                                           CameraCharacteristics cameraCharacteristics,
                                           Surface outputSurface,
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

         // Add target surface
         captureRequestBuilder.addTarget(outputSurface);

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
   @SuppressWarnings("javadoctype")
   public static class PhotoModule {
      @Provides @AppScope @IntoMap @ClassKey(PhotoStore.class)
      static Store<?> providePhotoStoreToMap(PhotoStore store) {
         return store;
      }
   }
}
