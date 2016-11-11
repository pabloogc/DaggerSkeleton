package com.bq.daggerskeleton.sample.app;

import android.app.Application;

import com.bq.daggerskeleton.BuildConfig;
import com.bq.daggerskeleton.flux.Dispatcher;
import com.bq.daggerskeleton.flux.InitAction;
import com.bq.daggerskeleton.flux.Store;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

public class App extends Application {

   private AppComponent appComponent;

   @Override public void onCreate() {
      super.onCreate();

      if (BuildConfig.DEBUG) {
         Timber.plant(new Timber.DebugTree());
      }

      long now = System.currentTimeMillis();
      appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
      ArrayList<Store<?>> stores = new ArrayList<>(appComponent.stores());

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
   }
}
