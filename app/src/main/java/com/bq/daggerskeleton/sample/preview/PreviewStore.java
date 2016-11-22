package com.bq.daggerskeleton.sample.preview;

import android.view.Surface;

import com.bq.daggerskeleton.flux.Dispatcher;
import com.bq.daggerskeleton.flux.Store;
import com.bq.daggerskeleton.sample.app.AppScope;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

/**
 * Store that handles the preview state and resources (Texture).
 */
@AppScope
public class PreviewStore extends Store<PreviewState> {

   @Inject
   public PreviewStore() {
      Dispatcher.subscribe(PreviewSurfaceDestroyedAction.class, a -> {
         PreviewState newState = new PreviewState(state());
         if (newState.previewTexture != null) {
            //Nothing to do here, surface auto releases, don't call release on it
            newState.previewTexture = null;
         }
         setState(newState);
      });

      Dispatcher.subscribe(PreviewSurfaceReadyAction.class, a -> {
         PreviewState newState = new PreviewState(state());
         newState.previewTexture = a.surfaceTexture;
         newState.previewSurface = new Surface(a.surfaceTexture);
         setState(newState);
      });

      Dispatcher.subscribe(PreviewSurfaceBufferCalculatedAction.class, a -> {
         PreviewState newState = new PreviewState(state());
         newState.previewSize = a.size;
         setState(newState);
      });
   }

   @Module
   @SuppressWarnings("javadoctype")
   public static class PreviewModule {
      @Provides @AppScope @IntoMap @ClassKey(PreviewStore.class)
      static Store<?> providePreviewStoreToMap(PreviewStore store) {
         return store;
      }
   }
}
