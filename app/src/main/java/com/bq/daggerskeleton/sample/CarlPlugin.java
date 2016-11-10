package com.bq.daggerskeleton.sample;

import com.bq.daggerskeleton.common.Plugin;
import com.bq.daggerskeleton.common.SimplePlugin;
import com.bq.daggerskeleton.common.PluginScope;
import com.bq.daggerskeleton.sample.rotation.RotationState;
import com.bq.daggerskeleton.sample.rotation.RotationStore;

import javax.inject.Inject;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import timber.log.Timber;

public interface CarlPlugin extends Plugin {

   void sayHello();

   enum Variant {
      VARIANT_1, VARIANT_2
   }

   @Module
   class CarlPluginModule {

      private final Variant variant;

      protected CarlPluginModule(Variant variant) {
         this.variant = variant;
      }

      @Provides @PluginScope
      CarlPlugin provideCarlPluginImplementation(Lazy<CarlPlugin1> c1, Lazy<CarlPlugin2> c2) {
         switch (variant) {
            case VARIANT_1:
               return c1.get();
            case VARIANT_2:
               return c2.get();
            default:
               throw new IllegalArgumentException();
         }
      }

      @Provides @PluginScope @IntoMap @ClassKey(CarlPlugin.class)
      static Plugin provideCarlPluginIntoMap(CarlPlugin carlPlugin) {
         return carlPlugin;
      }
   }

   @PluginScope
   class CarlPlugin1 extends SimplePlugin implements CarlPlugin {
      @Inject CarlPlugin1(RotationStore store) {
      }

      @Override public void sayHello() {
         Timber.d("Hello 1");
      }
   }

   @PluginScope
   class CarlPlugin2 extends SimplePlugin implements CarlPlugin {

      @Inject CarlPlugin2() {
      }

      @Override public void sayHello() {
         Timber.d("Hello 2");
      }
   }
}
