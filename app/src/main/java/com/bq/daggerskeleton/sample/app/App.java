package com.bq.daggerskeleton.sample.app;

import android.app.Application;
import android.os.Handler;
import android.os.HandlerThread;

import com.bq.daggerskeleton.flux.Dispatcher;
import com.bq.daggerskeleton.flux.InitAction;
import com.bq.daggerskeleton.flux.Store;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

/**
 * Application instance responsible for loading all Stores and sending {@link InitAction}.
 */
public class App extends Application {

   private AppComponent appComponent;

   @Override public void onCreate() {
      super.onCreate();

      Timber.plant(new Timber.DebugTree());
      Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
      Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
         Timber.tag("Fatal").e(e, "PID: %d", t.getId());
         defaultUncaughtExceptionHandler.uncaughtException(t, e);
      });

      long now = System.currentTimeMillis();
      appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
      ArrayList<Store<?>> stores = new ArrayList<>(appComponent.stores().values());

      Dispatcher.dispatch(new InitAction());

      final long elapsed = System.currentTimeMillis() - now;
      Timber.v("┌ Application with %2d stores loaded in %3d ms", stores.size(), elapsed);
      Timber.v("├────────────────────────────────────────────");
      for (Store<?> store : stores) {
         String boxChar = "├";
         if (store == stores.get(stores.size() - 1)) {
            boxChar = "└";
         }
         Timber.v("%s %s", boxChar, store.getClass().getSimpleName());
      }
   }

   public AppComponent getAppComponent() {
      return appComponent;
   }

   @Module
   static class AppModule {
      private final App app;

      AppModule(App app) {
         this.app = app;
      }

      @Provides App provideApp() {
         return app;
      }

      @Provides Application provideApplication() {
         return app;
      }

      @Provides @AppScope Handler provideBackgroundHandler() {
         HandlerThread bgThread = new HandlerThread("bg");
         bgThread.start();
         return new Handler(bgThread.getLooper());
      }
   }
}
