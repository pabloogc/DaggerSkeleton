package com.bq.daggerskeleton.sample;

import com.bq.daggerskeleton.common.SimplePlugin;
import com.bq.daggerskeleton.dagger.PluginScope;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;

@PluginScope
public class CarlPluginImpl1 extends SimplePlugin implements CarlPlugin {

   @Inject CarlPluginImpl1(AlicePlugin alicePlugin) {
   }


   @Module public static class CarlModuleImpl1 {

      private final boolean enabled;

      public CarlModuleImpl1(boolean enabled) {
         this.enabled = enabled;
      }

      @Provides @PluginScope @ElementsIntoSet
      Set<CarlPlugin> provideCarlPluginImpl1(Lazy<CarlPluginImpl1> impl) {
         if (enabled) {
            return new HashSet<CarlPlugin>(Collections.singletonList(impl.get()));
         } else {
            return new HashSet<>();
         }
      }
   }
}
