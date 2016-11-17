package com.bq.daggerskeleton.sample.preview;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
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
import timber.log.Timber;


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
      container = rootViewControllerPlugin.getPreviewContainer();
      View rootView = View.inflate(activity, R.layout.plugin_preview, rootViewControllerPlugin.getPreviewContainer());
      ButterKnife.bind(this, rootView);
      textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
         @Override
         public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            applyRatio();
            Dispatcher.dispatch(new PreviewSurfaceReadyAction(surface));

            //Now we wait for camera to open to calculate the appropriate buffer size
            track(cameraStore.flowable()
                  .startWith(cameraStore.state()) //It might have opened already
                  .filter(s -> s.cameraDevice != null)
                  .take(1)
                  .subscribe(s -> {
                     Dispatcher.dispatch(new PreviewSurfaceBufferCalculatedAction(calculateBufferSize()));
                  }));
         }

         @Override
         public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            Timber.v("Preview Surface changed: %dx%d", width, height);
            applyRatio(); //We don't care about the preview size, we already know it
         }

         @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            Dispatcher.dispatch(new PreviewSurfaceDestroyedAction());
            return true;
         }

         @Override public void onSurfaceTextureUpdated(SurfaceTexture surface) {
         }
      });
   }

   private Size calculateBufferSize() {
      CameraState s = cameraStore.state();
      if(s.cameraDevice == null) {
         throw new IllegalStateException("Need an open camera to calculate the buffer size based on capabilities");
      }

      final float ratio = AutoFitTextureView.RATIO_STANDARD;
      final CameraCharacteristics characteristics = s.availableCameras.get(s.cameraDevice.getId());
      final StreamConfigurationMap configurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

      if (configurationMap == null) {
         //No configuration map, should not happen but lets be safe here
         return new Size(640, 480); //4:3
      }

      Size captureSize = Collections.max(
            Arrays.asList(configurationMap.getOutputSizes(ImageFormat.JPEG)),
            new PreviewUtil.CompareSizesByArea());

      return PreviewUtil.chooseOptimalSize(
            configurationMap.getOutputSizes(SurfaceTexture.class), //Options
            textureView.getWidth(), //Target width
            (int) (textureView.getWidth() * ratio), //Calculated target height, keeping ratio
            container.getWidth(), //Max width
            container.getHeight(), //Max height
            captureSize //Aspect ratio
      );
   }

   private void applyRatio() {
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
      static Plugin providePreviewPlugin(PreviewPlugin plugin) {
         return plugin;
      }
   }
}
