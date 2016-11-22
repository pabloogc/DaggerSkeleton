package com.bq.daggerskeleton.sample.photo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.bq.daggerskeleton.R;
import com.bq.daggerskeleton.common.Plugin;
import com.bq.daggerskeleton.common.PluginScope;
import com.bq.daggerskeleton.common.SimplePlugin;
import com.bq.daggerskeleton.sample.core.RootViewControllerPlugin;
import com.bq.daggerskeleton.sample.hardware.CameraStore;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;


@PluginScope
@SuppressWarnings("javadoctype")
public class TakePhotoPlugin extends SimplePlugin {

   private final Activity activity;
   private final CameraStore cameraStore;
   private final RootViewControllerPlugin rootViewControllerPlugin;
   private ViewGroup container;

   @Inject
   TakePhotoPlugin(Activity activity, CameraStore cameraStore, RootViewControllerPlugin rootViewControllerPlugin) {
      this.activity = activity;
      this.cameraStore = cameraStore;
      this.rootViewControllerPlugin = rootViewControllerPlugin;
      this.container = rootViewControllerPlugin.getShutterContainer();
   }

   @Override public void onCreate(@Nullable Bundle savedInstanceState) {
      track(cameraStore.flowable()
            .filter(s -> s.cameraDevice != null)
            .take(1)
            .subscribe(s -> {
               View.inflate(activity, R.layout.shutter_button, container);
            }));
   }

   @Module
   @SuppressWarnings("javadoctype")
   public abstract static class TakePhotoModule {
      @Provides @PluginScope @IntoMap @ClassKey(TakePhotoPlugin.class)
      static Plugin providePhotoPlugin(TakePhotoPlugin plugin) {
         return plugin;
      }
   }
}
