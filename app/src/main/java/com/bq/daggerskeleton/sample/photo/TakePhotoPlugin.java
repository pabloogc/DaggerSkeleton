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
import com.bq.daggerskeleton.sample.hardware.session.SessionStore;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

/**
 * Plugin that takes care of the mediaType shutter button.
 * <p>
 * The plugin will trigger the needed action to take a mediaType when the shutter button is pressed.
 */
@PluginScope
public class TakePhotoPlugin extends SimplePlugin implements View.OnClickListener {

   private final Activity activity;
   private final SessionStore sessionStore;
   private final PhotoStore photoStore;
   private final RootViewControllerPlugin rootViewControllerPlugin;
   private ViewGroup container;
   private View shutterButton;

   @Inject
   TakePhotoPlugin(Activity activity, SessionStore sessionStore, PhotoStore photoStore, RootViewControllerPlugin rootViewControllerPlugin) {
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

      // Enable button when the status is different from "taking mediaType"
      photoStore.flowable()
            .subscribe(state -> {
               if (this.shutterButton != null) {
                  this.shutterButton.setEnabled(state.status != PhotoState.Status.TAKING);
               }
            });
   }

   @Override public void onClick(View v) {
      switch (sessionStore.state().outputMode) {
         case PHOTO:
            Dispatcher.dispatch(new TakePhotoAction());
            break;
         default:
            break;
      }
   }

   @Module
   @SuppressWarnings("javadoctype")
   public abstract static class TakePhotoModule {
      @Provides @PluginScope @IntoMap @ClassKey(TakePhotoPlugin.class)
      static Plugin provideTakePhotoPlugin(TakePhotoPlugin plugin) {
         return plugin;
      }
   }
}
