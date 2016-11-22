package com.bq.daggerskeleton.sample.photo;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.bq.daggerskeleton.R;
import com.bq.daggerskeleton.common.Plugin;
import com.bq.daggerskeleton.common.PluginScope;
import com.bq.daggerskeleton.common.SimplePlugin;
import com.bq.daggerskeleton.flux.Dispatcher;
import com.bq.daggerskeleton.sample.core.RootViewControllerPlugin;
import com.bq.daggerskeleton.sample.hardware.session.SessionState;
import com.bq.daggerskeleton.sample.hardware.session.SessionStore;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import timber.log.Timber;

/**
 * Plugin that takes care of the mediaType shutter button for burst mode photos.
 * <p>
 * The plugin will trigger the needed action to take a mediaType in burst mode when the shutter button is pressed.
 */
@PluginScope
public class TakeBurstPlugin extends SimplePlugin implements View.OnClickListener {

   private final Activity activity;
   private final SessionStore sessionStore;
   private final PhotoStore photoStore;
   private final RootViewControllerPlugin rootViewControllerPlugin;
   private ViewGroup container;
   private View shutterButton;
   private boolean isBursting = false;

   private long timestamp = System.currentTimeMillis();

   @Inject TakeBurstPlugin(Activity activity,
                           SessionStore sessionStore,
                           PhotoStore photoStore,
                           RootViewControllerPlugin rootViewControllerPlugin) {
      this.activity = activity;
      this.sessionStore = sessionStore;
      this.photoStore = photoStore;
      this.rootViewControllerPlugin = rootViewControllerPlugin;
   }

   @Override public void onComponentsCreated() {
      super.onComponentsCreated();

      this.container = rootViewControllerPlugin.getShutterContainer();

      track(sessionStore.flowable()
            .filter(s -> s.session != null)
            .take(1)
            .subscribe(s -> {
               View.inflate(activity, R.layout.shutter_button, container);
               this.shutterButton = container.findViewById(R.id.take_photo_button);
               this.shutterButton.setOnClickListener(this);
            }));

      photoStore.flowable()
            .subscribe(s -> {
               if (s.status == PhotoState.Status.IDLE && isBursting) {
                  long now = System.currentTimeMillis();
                  long diff = now - timestamp;
                  timestamp = now;
                  Timber.d("Photo time: %d", diff);
                  takePhoto();
               }
            });
   }

   @Override public void onClick(View v) {
      isBursting = !isBursting;
      takePhoto();
   }

   private void takePhoto() {
      if (SessionState.OutputMode.PHOTO == sessionStore.state().outputMode) {
         Dispatcher.dispatch(new TakePhotoAction());
      }
   }

   @Module
   @SuppressWarnings("javadoctype")
   public abstract static class TakePhotoModule {
      @Provides @PluginScope @IntoMap @ClassKey(TakeBurstPlugin.class)
      static Plugin providePhotoPlugin(TakeBurstPlugin plugin) {
         return plugin;
      }
   }
}
