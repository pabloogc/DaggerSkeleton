package com.bq.daggerskeleton.sample.app;

import com.bq.daggerskeleton.common.MainActivity;
import com.bq.daggerskeleton.sample.CameraComponent;
import com.bq.daggerskeleton.sample.CarlPlugin;
import com.bq.daggerskeleton.sample.flux.Store;
import com.bq.daggerskeleton.sample.rotation.RotationStore;

import java.util.Set;

import dagger.Component;

@Component(
      modules = {
            App.AppModule.class,
            RotationStore.RotationModule.class,
      }
)
@AppScope
public interface AppComponent {

   public Set<Store<?>> stores();

   CameraComponent cameraComponent(
         MainActivity.MainActivityModule activityModule,
         CarlPlugin.CarlPluginModule carlPluginModule
   );
}
