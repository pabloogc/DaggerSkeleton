package com.bq.daggerskeleton.sample.app;

import com.bq.daggerskeleton.common.MainActivity;
import com.bq.daggerskeleton.common.log.LoggerStore;
import com.bq.daggerskeleton.flux.Store;
import com.bq.daggerskeleton.sample.CameraComponent;
import com.bq.daggerskeleton.sample.hardware.CameraStore;
import com.bq.daggerskeleton.sample.hardware.session.SessionStore;
import com.bq.daggerskeleton.sample.photo.PhotoStore;
import com.bq.daggerskeleton.sample.preview.PreviewStore;
import com.bq.daggerskeleton.sample.rotation.RotationStore;
import com.bq.daggerskeleton.sample.storage.StorageStore;

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
            PreviewStore.PreviewModule.class,
            PhotoStore.PhotoModule.class,
            StorageStore.StorageModule.class,
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
