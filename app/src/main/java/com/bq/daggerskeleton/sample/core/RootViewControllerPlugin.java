package com.bq.daggerskeleton.sample.core;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.bq.daggerskeleton.R;
import com.bq.daggerskeleton.common.Plugin;
import com.bq.daggerskeleton.common.PluginProperties;
import com.bq.daggerskeleton.common.PluginScope;
import com.bq.daggerskeleton.common.SimplePlugin;
import com.bq.daggerskeleton.sample.rotation.RotationStore;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

@PluginScope
public class RootViewControllerPlugin extends SimplePlugin {

   private static final Interpolator ROTATION_INTERPOLATOR = new BounceInterpolator();

   private final Activity activity;
   private final RotationStore rotationStore;

   @BindView(R.id.side_controls_container) ViewGroup sideControlsContainer;
   @BindView(R.id.shutter_container) ViewGroup shutterContainer;
   @BindView(R.id.preview_container) ViewGroup previewContainer;

   @Inject RootViewControllerPlugin(Activity activity, RotationStore rotationStore) {
      this.activity = activity;
      this.rotationStore = rotationStore;
   }

   @Override public PluginProperties getProperties() {
      return PluginProperties.HIGH;
   }

   @Override public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      activity.setContentView(R.layout.activity_container);
      ButterKnife.bind(this, activity);

      track(rotationStore.flowable().subscribe(r -> {
         animateRotation(shutterContainer, r.deviceAccumulatedRotation);
         animateRotation(sideControlsContainer, r.deviceAccumulatedRotation);
      }));
   }

   private void animateRotation(ViewGroup container, float rotation) {
      for (int i = 0; i < container.getChildCount(); i++) {
         View view = container.getChildAt(i);
         view.animate()
               .setDuration(1000)
               .setInterpolator(ROTATION_INTERPOLATOR)
               .rotation(rotation);
      }
   }

   public ViewGroup getPreviewContainer() {
      return previewContainer;
   }

   public ViewGroup getShutterContainer() {
      return shutterContainer;
   }

   public ViewGroup getSideControlsContainer() {
      return sideControlsContainer;
   }

   @Module
   public static abstract class RootViewControllerModule {
      @PluginScope @Provides @IntoMap @ClassKey(RootViewControllerPlugin.class)
      static Plugin provideRootViewControllerPlugin(RootViewControllerPlugin plugin) {
         return plugin;
      }
   }
}
