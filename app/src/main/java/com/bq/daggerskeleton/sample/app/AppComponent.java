package com.bq.daggerskeleton.sample.app;

import com.bq.daggerskeleton.common.MainActivity;
import com.bq.daggerskeleton.common.log.LoggerStore;
import com.bq.daggerskeleton.flux.Store;
import com.bq.daggerskeleton.sample.CameraComponent;
import com.bq.daggerskeleton.sample.hardware.CameraStore;
import com.bq.daggerskeleton.sample.hardware.session.SessionStore;
import com.bq.daggerskeleton.sample.rotation.RotationStore;

import java.util.Map;

import dagger.Component;

/**
 * Root component holding app singletons (Stores).
 */
@Component(
      modules = {
            App.AppModule.class,
            RotationStore.RotationModule.class,
            LoggerStore.LoggerModule.class,
            CameraStore.CameraModule.class,
            SessionStore.SessionModule.class,
      }
)
@AppScope
public interface AppComponent {

   /**
    * The loaded stores, indexed by type.
    */
   Map<Class<?>, Store<?>> stores();

   /**
    * Default camera component.
    */
   CameraComponent cameraComponent(
         MainActivity.MainActivityModule activityModule
   );
}
