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
public class CarlPluginImpl2 extends SimplePlugin implements CarlPlugin {

   @Inject CarlPluginImpl2() {
   }

   @Module public static class CarlModuleImpl2 {

      private final boolean enabled;

      public CarlModuleImpl2(boolean enabled) {
         this.enabled = enabled;
      }

      @Provides @PluginScope @ElementsIntoSet
      Set<CarlPlugin> provideCarlImplementation2(Lazy<CarlPluginImpl2> impl) {
         if (enabled) {
            return new HashSet<CarlPlugin>(Collections.singletonList(impl.get()));
         } else {
            return new HashSet<>();
         }
      }
   }
}
