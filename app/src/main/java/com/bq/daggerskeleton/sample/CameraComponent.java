package com.bq.daggerskeleton.sample;


import com.bq.daggerskeleton.common.MainActivity;
import com.bq.daggerskeleton.common.Plugin;
import com.bq.daggerskeleton.common.LoggerPlugin;
import com.bq.daggerskeleton.common.PluginScope;

import java.util.Map;

import dagger.Subcomponent;

@Subcomponent(
      modules = {
            MainActivity.MainActivityModule.class,
            AlicePlugin.AliceModule.class,
            BobPlugin.BobModule.class,

            CarlPlugin.CarlPluginModule.class,
            LoggerPlugin.LoggerModule.class,
      }
)
@PluginScope
public interface CameraComponent {
   Map<Class<?>, Plugin> pluginMap();

   void inject(MainActivity activity);
}
