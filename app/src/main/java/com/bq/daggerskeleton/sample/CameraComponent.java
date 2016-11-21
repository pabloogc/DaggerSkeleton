package com.bq.daggerskeleton.sample;


import com.bq.daggerskeleton.common.MainActivity;
import com.bq.daggerskeleton.common.Plugin;
import com.bq.daggerskeleton.common.PluginScope;
import com.bq.daggerskeleton.common.log.LoggerPlugin;
import com.bq.daggerskeleton.common.log.LoggerStore;
import com.bq.daggerskeleton.sample.core.RootViewControllerPlugin;
import com.bq.daggerskeleton.sample.misc.RequestPermissionPlugin;
import com.bq.daggerskeleton.sample.photo.TakePhotoPlugin;
import com.bq.daggerskeleton.sample.preview.PreviewPlugin;

import java.util.Map;

import dagger.Subcomponent;

/**
 * Default camera component.
 */
@Subcomponent(
      modules = {
            MainActivity.MainActivityModule.class,
            LoggerPlugin.LoggerPluginModule.class,

            RootViewControllerPlugin.RootViewControllerModule.class,
            RequestPermissionPlugin.RequestPermissionModule.class,

            PreviewPlugin.PreviewModule.class,
            TakePhotoPlugin.TakePhotoModule.class,

            LoggerStore.LoggerModule.class,
      }
)
@PluginScope
@SuppressWarnings("Checkstyle")
public interface CameraComponent {
   /**
    * Loaded plugins, indexed by type.
    */
   Map<Class<?>, Plugin> pluginMap();

   /**
    * Inject the only activity. We actually don't need this, since the plugin map is the
    * only dependency.
    */
   void inject(MainActivity activity);
}
