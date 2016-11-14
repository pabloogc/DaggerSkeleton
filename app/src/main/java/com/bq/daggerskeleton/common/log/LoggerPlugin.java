package com.bq.daggerskeleton.common.log;

import android.content.Context;

import com.bq.daggerskeleton.common.Plugin;
import com.bq.daggerskeleton.common.PluginProperties;
import com.bq.daggerskeleton.common.PluginScope;
import com.bq.daggerskeleton.common.SimplePlugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.util.Log.d;
import static android.util.Log.e;

@PluginScope
public final class LoggerPlugin extends SimplePlugin {


   private final LoggerStore loggerStore;
   private final Lazy<Map<Class<?>, Plugin>> pluginMap;
   private final Context context;

   @Inject
   LoggerPlugin(Context context, LoggerStore loggerStore, Lazy<Map<Class<?>, Plugin>> pluginMap) {
      this.loggerStore = loggerStore;
      this.pluginMap = pluginMap;
      this.context = context;
   }

   @Override public PluginProperties getProperties() {
      return PluginProperties.MAX;
   }

   @Override
   public void onComponentsCreated() {
      scanPlugins()
            .subscribeOn(Schedulers.io())
            .subscribe();
   }

   @Override public void onPause() {
      FileLogger logger = loggerStore.state().fileLogger;
      if (logger != null) {
         logger.flush();
      }
   }

   private Completable scanPlugins() {
      return Completable.create(e -> registerToPluginsObservables());
   }

   @SuppressWarnings("unchecked")
   private void registerToPluginsObservables() {
      long start = System.nanoTime();

      for (Object object : pluginMap.get().values()) {
         final Class<?> clazz = object.getClass();
         for (Field field : clazz.getDeclaredFields()) {

            if (field.getAnnotation(AutoLog.class) == null) continue;
            final String tag = clazz.getSimpleName();
            final String observableName = field.getName();

            try {
               field.setAccessible(true);
               Object pluginField = field.get(object);
               Disposable disposable = LoggerStore.subscribeToObservableUnsafe(pluginField, tag, observableName);
               if (disposable != null) track(disposable);
            } catch (Exception e) {
               Timber.e(e);
            }
         }
      }

      long elapsed = System.nanoTime() - start;
      Timber.d("Logger scan completed in %d ms", TimeUnit.MILLISECONDS.convert(elapsed, TimeUnit.NANOSECONDS));
   }

   /**
    * Annotation for automatic subscribes to register for callbacks.
    */
   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.METHOD, ElementType.FIELD})
   public @interface AutoLog {

   }

   @Module
   public static abstract class LoggerPluginModule {
      @Provides @PluginScope @IntoMap @ClassKey(LoggerPlugin.class)
      static Plugin provideLoggerPlugin(LoggerPlugin plugin) {
         return plugin;
      }
   }
}
