package com.bq.daggerskeleton.sample;

import android.content.Context;

import com.bq.daggerskeleton.common.Plugin;
import com.bq.daggerskeleton.common.LoggerPlugin;
import com.bq.daggerskeleton.common.SimplePlugin;
import com.bq.daggerskeleton.dagger.PluginScope;

import javax.inject.Inject;

import dagger.Lazy;
import dagger.MapKey;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;


@PluginScope
public class AlicePlugin extends SimplePlugin {


   @LoggerPlugin.AutoLog
   private final Flowable<String> sampleFlowable = Flowable.interval(1, java.util.concurrent.TimeUnit.SECONDS)
         .map(new Function<Long, String>() {
            @Override
            public String apply(Long aLong) throws Exception {
               return "Hello " + String.valueOf(aLong);
            }
         });

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
