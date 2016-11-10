package com.bq.daggerskeleton.sample.app;

import android.app.Application;
import android.app.ApplicationErrorReport;

import dagger.Module;
import dagger.Provides;

public class App extends Application {

   private AppComponent appComponent;

   @Override public void onCreate() {
      super.onCreate();
      appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
      appComponent.stores();
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
