package com.bq.daggerskeleton.sample.misc;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.bq.daggerskeleton.common.Plugin;
import com.bq.daggerskeleton.common.PluginScope;
import com.bq.daggerskeleton.common.SimplePlugin;
import com.bq.daggerskeleton.flux.Dispatcher;
import com.bq.daggerskeleton.sample.hardware.CameraPermissionChangedAction;
import com.tbruyelle.rxpermissions2.RxPermissions;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

/**
 * Plugin that checks if the camera has permissions, shows the dialog and closes
 * the camera if it fails.
 */
public class RequestPermissionPlugin extends SimplePlugin {

   private final Activity activity;

   @Inject RequestPermissionPlugin(Activity activity) {
      this.activity = activity;
   }

   @Override public void onCreate(@Nullable Bundle savedInstanceState) {
      boolean cameraGranted = RxPermissions.getInstance(activity).isGranted(Manifest.permission.CAMERA);
      Dispatcher.dispatch(new CameraPermissionChangedAction(cameraGranted));
      if (cameraGranted) return; //No need to ask

      track(RxPermissions.getInstance(activity)
            .request(Manifest.permission.CAMERA)
            .subscribe(granted -> {
               if (!granted) {
                  Toast.makeText(activity, "Missing Required Permission", Toast.LENGTH_SHORT).show();
                  activity.finish();
               }
               Dispatcher.dispatch(new CameraPermissionChangedAction(granted));
            }));
   }

   @Module
   @SuppressWarnings("javadoctype")
   public abstract static class RequestPermissionModule {
      @PluginScope @Provides @IntoMap @ClassKey(RequestPermissionPlugin.class)
      static Plugin provideRequestPermissionModule(RequestPermissionPlugin plugin) {
         return plugin;
      }
   }
}
