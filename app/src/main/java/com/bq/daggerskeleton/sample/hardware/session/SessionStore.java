package com.bq.daggerskeleton.sample.hardware.session;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Size;

import com.bq.daggerskeleton.flux.Dispatcher;
import com.bq.daggerskeleton.flux.Store;
import com.bq.daggerskeleton.sample.app.AppScope;
import com.bq.daggerskeleton.sample.hardware.CameraState;
import com.bq.daggerskeleton.sample.hardware.CameraStore;
import com.bq.daggerskeleton.sample.hardware.CloseCameraAction;

import java.util.Collections;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import timber.log.Timber;

/**
 * Session opening and closing logic.
 */
@AppScope
public class SessionStore extends Store<SessionState> {

   private final CameraStore cameraStore;
   private final Handler backgroundHandler;

   @Inject SessionStore(CameraStore cameraStore, Handler backgroundHandler) {
      this.cameraStore = cameraStore;
      this.backgroundHandler = backgroundHandler;

      Dispatcher.subscribe(Dispatcher.VERY_HIGH_PRIORITY,
            CloseCameraAction.class, a -> setState(releaseSession(state())));

      Dispatcher.subscribe(SessionChangedAction.class, a -> {
         SessionState newState = new SessionState(state());
         newState.session = a.session;
         newState.status = a.status;
         newState.error = a.error;
         setState(newState);
      });

      this.cameraStore.flowable().subscribe(a -> {
         tryToStartSession();
      });
   }

   /**
    * Start the session if preconditions are met. For this example, the preview surface is ready and
    * the camera opened.
    */
   private void tryToStartSession() {
      //Preconditions
      CameraState cameraState = cameraStore.state();

      if (state().status.isReadyOrOpening()
            || cameraState.previewTexture == null
            || cameraState.cameraDevice == null
            || cameraState.previewSize == null) {
         return;
      }

      try {
         CaptureRequest.Builder request;
         try {
            request = cameraState.cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
         } catch (SecurityException e) {
            // STOPSHIP: 16/11/2016
            // TODO: 16/11/2016 Move to an error status and notify user properly
            Timber.e(e);
            throw new IllegalStateException("Failed to create session, "
                  + "there is a race condition between 2 camera apps");
         }

         request.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

         //Configure preview surface
         Size previewSize = cameraState.previewSize;
         cameraState.previewTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
         request.addTarget(cameraState.previewSurface);

         cameraState.cameraDevice
               .createCaptureSession(Collections.singletonList(cameraState.previewSurface), new CameraCaptureSession.StateCallback() {
                  @Override public void onConfigured(@NonNull CameraCaptureSession session) {
                     try {
                        session.getInputSurface();
                        //This call is irrelevant,
                        //however session might have closed and this will throw an IllegalStateException.
                        //This happens if another camera app (or this one in another PID) takes control
                        //of the camera while its opening
                     } catch (IllegalStateException e) {
                        Timber.e("Another process took control of the camera while creating the session, aborting!");
                        Dispatcher.dispatchOnUi(new SessionChangedAction(null, SessionState.Status.ERROR, e));
                        return;
                     }
                     Dispatcher.dispatchOnUi(new SessionChangedAction(session, SessionState.Status.READY));
                     try {
                        session.setRepeatingRequest(request.build(), null, backgroundHandler);
                     } catch (CameraAccessException e) {
                        Timber.e(e);
                        Dispatcher.dispatchOnUi(new SessionChangedAction(null, SessionState.Status.ERROR, e));
                     }
                  }

                  @Override public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                     Dispatcher.dispatchOnUi(new SessionChangedAction(session, SessionState.Status.ERROR));
                  }
               }, backgroundHandler);

         //Now we are opening
         SessionState newState = new SessionState(state());
         newState.status = SessionState.Status.OPENING;
         setState(newState);

      } catch (IllegalStateException | CameraAccessException e) {
         Timber.e(e);
      }
   }

   private SessionState releaseSession(SessionState state) {
      SessionState newState = new SessionState(state);
      if (newState.status == SessionState.Status.READY) {
         try {
            newState.session.close();
         } catch (IllegalStateException e) {
            //Will throw IllegalStateException if it is already closed
            //due to a race condition not worth controlling.
            //There is no way to check the camera status other than capturing the exception
            Timber.e(e, "Error trying to release the session");
         }
         newState.session = null;
         newState.error = null;
         newState.status = SessionState.Status.NO_SESSION;
      }
      return newState;
   }

   @Module
   public static class SessionModule {
      @Provides @AppScope @IntoMap @ClassKey(SessionStore.class)
      static Store<?> provideSessionStoreToSet(SessionStore store) {
         return store;
      }
   }
}
