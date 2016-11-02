package com.bq.daggerskeleton.sample;


import com.bq.daggerskeleton.common.MainActivity;
import com.bq.daggerskeleton.common.Plugin;
import com.bq.daggerskeleton.common.LoggerPlugin;
import com.bq.daggerskeleton.dagger.PluginScope;
import com.bq.daggerskeleton.sample.AlicePlugin;
import com.bq.daggerskeleton.sample.BobPlugin;
import com.bq.daggerskeleton.sample.CarlPlugin;
import com.bq.daggerskeleton.sample.CarlPluginImpl1;
import com.bq.daggerskeleton.sample.CarlPluginImpl2;

import java.util.Map;

import dagger.Component;

@Component(
      modules = {
            MainActivity.MainActivityModule.class,
            AlicePlugin.AliceModule.class,
            BobPlugin.BobModule.class,

            CarlPlugin.CarlPluginModule.class,
            CarlPluginImpl1.CarlModuleImpl1.class,
            CarlPluginImpl2.CarlModuleImpl2.class,

            LoggerPlugin.LoggerModule.class,
      }
)
@PluginScope
public interface MainActivityComponent {
   Map<Class<?>, Plugin> pluginMap();

   void inject(MainActivity activity);
}
