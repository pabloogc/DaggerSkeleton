package com.bq.daggerskeleton.sample;

import com.bq.daggerskeleton.common.Plugin;
import com.bq.daggerskeleton.common.SimplePlugin;
import com.bq.daggerskeleton.dagger.PluginScope;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

@PluginScope
public class BobPlugin extends SimplePlugin {

   private final AlicePlugin alicePlugin;

   @Inject BobPlugin(AlicePlugin alicePlugin) {
      this.alicePlugin = alicePlugin;
   }

   @Module public static abstract class BobModule {
      @Provides @PluginScope @IntoMap @ClassKey(BobPlugin.class)
      static Plugin provideBob(BobPlugin BobPlugin) {
         return BobPlugin;
      }
   }
}
