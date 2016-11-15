package com.bq.daggerskeleton.sample.preview;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Size;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.bq.daggerskeleton.R;
import com.bq.daggerskeleton.common.Plugin;
import com.bq.daggerskeleton.common.SimplePlugin;
import com.bq.daggerskeleton.common.PluginScope;
import com.bq.daggerskeleton.flux.Dispatcher;
import com.bq.daggerskeleton.sample.core.RootViewControllerPlugin;
import com.bq.daggerskeleton.sample.hardware.CameraState;
import com.bq.daggerskeleton.sample.hardware.CameraStore;
import com.bq.daggerskeleton.sample.hardware.CloseCameraAction;
import com.bq.daggerskeleton.sample.hardware.OpenCameraAction;
import com.bq.daggerskeleton.sample.hardware.PreviewSurfaceReadyAction;
import com.bq.daggerskeleton.sample.hardware.PreviewSurfaceDestroyedAction;
import com.bq.daggerskeleton.sample.views.AutoFitTextureView;

import java.util.Arrays;
import java.util.Collections;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;


@PluginScope
public class PreviewPlugin extends SimplePlugin {

   private final Activity activity;
   private final CameraStore cameraStore;
   private final RootViewControllerPlugin rootViewControllerPlugin;
   private ViewGroup container;
   @BindView(R.id.preview_texture) AutoFitTextureView textureView;

   @Inject
   PreviewPlugin(Activity activity, CameraStore cameraStore, RootViewControllerPlugin rootViewControllerPlugin) {
      this.activity = activity;
      this.cameraStore = cameraStore;
      this.rootViewControllerPlugin = rootViewControllerPlugin;
   }

   @Override public void onCreate(@Nullable Bundle savedInstanceState) {
      View rootView = View.inflate(activity, R.layout.plugin_preview, rootViewControllerPlugin.getPreviewContainer());
      ButterKnife.bind(this, rootView);
      container = rootViewControllerPlugin.getPreviewContainer();
      textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
         @Override
         public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Size bufferPreviewSize = configureTextureView();
            Dispatcher.dispatch(new PreviewSurfaceReadyAction(surface, bufferPreviewSize.getWidth(), bufferPreviewSize.getHeight()));
         }

         @Override
         public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            configureMatrix();
         }

         @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            Dispatcher.dispatch(new PreviewSurfaceDestroyedAction());
            return true;
         }

         @Override public void onSurfaceTextureUpdated(SurfaceTexture surface) {
         }
      });
   }

   private Size configureTextureView() {
      CameraState s = cameraStore.state();
      CameraCharacteristics characteristics = s.availableCameras.get(s.selectedCamera);
      StreamConfigurationMap configurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

      if (configurationMap == null) return new Size(640, 480); //4:3

      Size captureSize = Collections.max(
            Arrays.asList(configurationMap.getOutputSizes(ImageFormat.JPEG)),
            new PreviewUtil.CompareSizesByArea());

      final float ratio = AutoFitTextureView.RATIO_STANDARD;

      Size bufferSize = PreviewUtil.chooseOptimalSize(
            configurationMap.getOutputSizes(ImageFormat.JPEG), //options
            textureView.getWidth(),
            (int) (textureView.getWidth() * ratio),
            container.getWidth(),
            container.getHeight(),
            captureSize
      );

      textureView.getSurfaceTexture().setDefaultBufferSize(bufferSize.getWidth(), bufferSize.getHeight());
      textureView.setAspectRatio(4f / 3f);
      return bufferSize;
   }

   private void configureMatrix() {
      textureView.setAspectRatio(4f / 3f);
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
