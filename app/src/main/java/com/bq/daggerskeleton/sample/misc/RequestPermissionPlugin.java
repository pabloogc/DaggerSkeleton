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

   private static final String[] NEEDED_PERMISSIONS =
         new String[]{
               Manifest.permission.CAMERA,
               Manifest.permission.WRITE_EXTERNAL_STORAGE};

   private final Activity activity;
   private final RxPermissions rxPermissionsInstance;

   @Inject RequestPermissionPlugin(Activity activity) {
      this.activity = activity;
      this.rxPermissionsInstance = RxPermissions.getInstance(activity);
   }

   @Override public void onCreate(@Nullable Bundle savedInstanceState) {
      boolean permissionsGranted = arePermissionsGranted();
      Dispatcher.dispatch(new PermissionsChangedAction(permissionsGranted));

      if (permissionsGranted) return; //No need to ask

      track(rxPermissionsInstance
            .request(NEEDED_PERMISSIONS)
            .subscribe(granted -> {
               if (!granted) {
                  Toast.makeText(activity, "Missing Required Permission", Toast.LENGTH_SHORT).show();
                  activity.finish();
               }
               Dispatcher.dispatch(new PermissionsChangedAction(granted));
            }));
   }

   private boolean arePermissionsGranted() {
      for (String permission : NEEDED_PERMISSIONS) {
         if (!rxPermissionsInstance.isGranted(permission)) {
            return false;
         }
      }
      return true;
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
