package com.bq.daggerskeleton.sample.preview;

import com.bq.daggerskeleton.common.Plugin;
import com.bq.daggerskeleton.common.SimplePlugin;
import com.bq.daggerskeleton.common.PluginScope;
import com.bq.daggerskeleton.sample.BobPlugin;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;


@PluginScope
public class PreviewPlugin extends SimplePlugin {

   @Inject PreviewPlugin(BobPlugin bobPlugin) {

   }

   @Module
   public static abstract class PreviewModule {
      @Provides @PluginScope @IntoMap @ClassKey(PreviewPlugin.class)
      static Plugin provideAlice(PreviewPlugin previewPlugin) {
         return previewPlugin;
      }
   }
}
