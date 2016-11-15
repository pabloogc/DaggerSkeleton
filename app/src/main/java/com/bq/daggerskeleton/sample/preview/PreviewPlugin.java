package com.bq.daggerskeleton.sample.preview;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.TextureView;
import android.view.View;

import com.bq.daggerskeleton.R;
import com.bq.daggerskeleton.common.Plugin;
import com.bq.daggerskeleton.common.SimplePlugin;
import com.bq.daggerskeleton.common.PluginScope;
import com.bq.daggerskeleton.flux.Dispatcher;
import com.bq.daggerskeleton.sample.core.RootViewControllerPlugin;
import com.bq.daggerskeleton.sample.hardware.CloseCameraAction;
import com.bq.daggerskeleton.sample.hardware.OpenCameraAction;
import com.bq.daggerskeleton.sample.hardware.PreviewSurfaceChangedAction;
import com.bq.daggerskeleton.sample.hardware.PreviewSurfaceDestroyedAction;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;


@PluginScope
public class PreviewPlugin extends SimplePlugin {

   private final Context context;
   private final RootViewControllerPlugin rootViewControllerPlugin;
   @BindView(R.id.preview_texture) TextureView textureView;

   @Inject PreviewPlugin(Context context, RootViewControllerPlugin rootViewControllerPlugin) {
      this.context = context;
      this.rootViewControllerPlugin = rootViewControllerPlugin;
   }

   @Override public void onCreate(@Nullable Bundle savedInstanceState) {
      View rootView = View.inflate(context, R.layout.plugin_preview, rootViewControllerPlugin.getPreviewContainer());
      ButterKnife.bind(this, rootView);

      textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {

         @Override
         public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Dispatcher.dispatch(new PreviewSurfaceChangedAction(surface, width, height));
         }

         @Override
         public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            Dispatcher.dispatch(new PreviewSurfaceChangedAction(surface, width, height));
         }

         @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            Dispatcher.dispatch(new PreviewSurfaceDestroyedAction());
            return true;
         }

         @Override public void onSurfaceTextureUpdated(SurfaceTexture surface) {
         }
      });
   }

   @Override public void onResume() {
      Dispatcher.dispatch(new OpenCameraAction());
   }

   @Override public void onPause() {
      Dispatcher.dispatch(new CloseCameraAction());
   }

   @Module
   public static abstract class PreviewModule {
      @Provides @PluginScope @IntoMap @ClassKey(PreviewPlugin.class)
      static Plugin provideAlice(PreviewPlugin previewPlugin) {
         return previewPlugin;
      }
   }
}
