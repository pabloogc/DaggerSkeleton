package com.bq.daggerskeleton.sample.hardware.session;

import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.util.Pair;
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
import dagger.multibindings.IntoSet;
import durdinapps.rxcamera2.wrappers.RxConfigureSessionEvent;
import timber.log.Timber;

@AppScope
public class SessionStore extends Store<SessionState> {

   private final CameraStore cameraStore;
   private final Handler backgroundHandler;

   @Override protected SessionState initialState() {
      return new SessionState();
   }

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
    * the camera opened
    */
   private void tryToStartSession() {
      //Preconditions
      CameraState cameraState = cameraStore.state();

      if (cameraState.sessionState.status.isReadyOrOpening()
              || cameraState.previewTexture == null
              || cameraState.cameraDevice == null
              || cameraState.previewSize == null) {
         return;
      }

      cameraState.cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
              .flatMap(builder -> {
                 builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                 //Configure preview surface
                 Size previewSize = cameraState.previewSize;
                 cameraState.previewTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
                 builder.addTarget(cameraState.previewSurface);
                 return cameraState.cameraDevice.createCaptureSession(Collections.singletonList(cameraState.previewSurface), builder, backgroundHandler)
                         .map((a) -> Pair.create(builder, a));
              })
              .subscribe(pair -> {
                 RxConfigureSessionEvent rxConfigureSessionEvent = pair.second;
                 CaptureRequest.Builder builder = pair.first;
                 switch (rxConfigureSessionEvent.eventType) {
                    case CONFIGURE:
                       try {
                          rxConfigureSessionEvent.session.getCaptureSession().getInputSurface();
                          //This call is irrelevant,
                          //however session might have closed and this will throw an IllegalStateException.
                          //This happens if another camera app (or this one in another PID) takes control
                          //of the camera while its opening
                       } catch (IllegalStateException e) {
                          Timber.e("Another process took control of the camera while creating the session, aborting!");
                          Dispatcher.dispatchOnUi(new SessionChangedAction(null, SessionState.Status.ERROR, e));
                       }
                       Dispatcher.dispatchOnUi(new SessionChangedAction(rxConfigureSessionEvent.session, SessionState.Status.READY));

                       rxConfigureSessionEvent.session.setRepeatingRequest(builder.build(), backgroundHandler).subscribe();
                       break;

                    case CONFIGURE_FAILED:
                       Dispatcher.dispatchOnUi(new SessionChangedAction(rxConfigureSessionEvent.session, SessionState.Status.ERROR));
                       break;
                 }
                 //Now we are opening
                 SessionState newState = new SessionState(state());
                 newState.status = SessionState.Status.OPENING;
                 setState(newState);
              });
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
      @Provides @AppScope @IntoSet
      static Store<?> provideSessionStoreToSet(SessionStore store) {
         return store;
      }
   }
}
