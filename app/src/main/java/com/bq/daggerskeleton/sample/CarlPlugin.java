package com.bq.daggerskeleton.sample;

import com.bq.daggerskeleton.common.Plugin;
import com.bq.daggerskeleton.dagger.DaggerUtil;
import com.bq.daggerskeleton.dagger.PluginScope;

import java.util.Set;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

public interface CarlPlugin extends Plugin {

   @Module abstract class CarlPluginModule {
      @Provides @PluginScope
      static CarlPlugin provideCarlPluginImplementation(Set<CarlPlugin> implementations) {
         return DaggerUtil.getSingleValue(implementations);
      }

      @Provides @PluginScope @IntoMap @ClassKey(CarlPlugin.class)
      static Plugin provideCarlPluginIntoMap(CarlPlugin carlPlugin) {
         return carlPlugin;
      }
   }
}
