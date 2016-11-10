package com.bq.daggerskeleton.sample;

import com.bq.daggerskeleton.common.Plugin;
import com.bq.daggerskeleton.common.LoggerPlugin;
import com.bq.daggerskeleton.common.SimplePlugin;
import com.bq.daggerskeleton.common.PluginScope;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;


@PluginScope
public class AlicePlugin extends SimplePlugin {

   @Inject AlicePlugin(BobPlugin bobPlugin) {

   }

   @Module
   static abstract class AliceModule {
      @Provides @PluginScope @IntoMap @ClassKey(AlicePlugin.class)
      static Plugin provideAlice(AlicePlugin alicePlugin) {
         return alicePlugin;
      }
   }
}
