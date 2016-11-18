package com.bq.daggerskeleton.sample.app;

import com.bq.daggerskeleton.common.log.LoggerStore;
import com.bq.daggerskeleton.common.MainActivity;
import com.bq.daggerskeleton.sample.CameraComponent;
import com.bq.daggerskeleton.flux.Store;
import com.bq.daggerskeleton.sample.hardware.CameraStore;
import com.bq.daggerskeleton.sample.hardware.session.SessionStore;
import com.bq.daggerskeleton.sample.rotation.RotationStore;

import java.util.Set;

import dagger.Component;

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

   Set<Store<?>> stores();

   CameraComponent cameraComponent(
         MainActivity.MainActivityModule activityModule
   );
}
