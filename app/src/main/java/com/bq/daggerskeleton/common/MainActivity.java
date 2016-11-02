package com.bq.daggerskeleton.common;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.bq.daggerskeleton.R;
import com.bq.daggerskeleton.sample.DaggerMainActivityComponent;
import com.bq.daggerskeleton.sample.MainActivityComponent;
import com.bq.daggerskeleton.sample.CarlPluginImpl1;
import com.bq.daggerskeleton.sample.CarlPluginImpl2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

   private static final String TAG = "MainActivity";
   private static final String LC_TAG = "LifeCycle";

   private static final String ARG_PLUGIN_SAVED_STATES = "pluginStates";

   //######################
   // Plugins
   //######################
   private final List<Plugin> pluginList = new ArrayList<>();
   private final List<Plugin> componentBackList = new ArrayList<>();
   private final List<Plugin> componentTouchList = new ArrayList<>();

   //######################
   // Misc
   //######################
   private SharedEvent<MotionEvent> sharedMotionEvent = SharedEvent.create();
   private SharedEvent<KeyEvent> sharedKeyEvent = SharedEvent.create();
   private SharedEvent<Void> sharedBackEvent = SharedEvent.create();


   private MainActivityComponent mainActivityComponent;
   @Inject Map<Class<?>, Plugin> pluginMap;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      mainActivityComponent = DaggerMainActivityComponent.builder()
            .mainActivityModule(new MainActivityModule(this))
            .carlModuleImpl1(new CarlPluginImpl1.CarlModuleImpl1(true))
            .carlModuleImpl2(new CarlPluginImpl2.CarlModuleImpl2(false))
            .build();
      mainActivityComponent.inject(this);

      setContentView(R.layout.activity_container);

      //#############
      //Component Initialization
      //#############

      pluginList.addAll(pluginMap.values());

      ArrayList<Bundle> componentSavedStates = null;
      if (savedInstanceState != null) {
         componentSavedStates = savedInstanceState.getParcelableArrayList(ARG_PLUGIN_SAVED_STATES);
      }

      Timber.tag(LC_TAG).d("onCreate");
      for (int i = 0; i < pluginList.size(); i++) {
         Bundle state = null;
         if (componentSavedStates != null) state = componentSavedStates.get(i);
         pluginList.get(i).onCreate(state);
      }

      Timber.tag(LC_TAG).d("onCreateView");
      for (Plugin component : pluginList) {
         component.onCreateView();
      }
   }

   //###############################################
   // Life-Cycle
   //###############################################

   @Override
   public void onPostCreate(Bundle savedInstanceState) {
      super.onPostCreate(savedInstanceState);
      Timber.tag(LC_TAG).d("onPostCreate");
      for (Plugin component : pluginList) {
         component.onPostCreate();
      }
      Timber.tag(LC_TAG).d("onComponentsReady");
      for (Plugin component : pluginList) {
         component.onComponentsReady();
      }
   }

   @Override
   protected void onStart() {
      super.onStart();
      Timber.tag(LC_TAG).d("onStart");
      for (Plugin component : pluginList) {
         component.onStart();
      }
   }

   @Override
   protected void onResume() {
      super.onResume();
      Timber.tag(LC_TAG).d("onResume");
      for (Plugin component : pluginList) {
         component.onResume();
      }
   }

   @Override
   protected void onPause() {
      super.onPause();
      Timber.tag(LC_TAG).d("onPause");
      for (Plugin component : pluginList) {
         component.onPause();
      }
   }

   @Override
   protected void onStop() {
      super.onStop();
      Timber.tag(LC_TAG).d("onStop");
      for (Plugin component : pluginList) {
         component.onStop();
      }
   }

   @Override
   protected void onSaveInstanceState(@NonNull Bundle outState) {
      super.onSaveInstanceState(outState);
      Timber.tag(LC_TAG).d("onSaveInstanceState");
      ArrayList<Bundle> states = new ArrayList<>();
      for (Plugin component : pluginList) {
         Bundle componentBundle = new Bundle();
         component.onSaveInstanceState(componentBundle);
         states.add(componentBundle);
      }
      outState.putParcelableArrayList(ARG_PLUGIN_SAVED_STATES, states);
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
      Timber.tag(LC_TAG).d("onDestroy");
      for (Plugin component : pluginList) {
         component.onDestroy();
      }
   }

   @Override
   public void onConfigurationChanged(@NonNull Configuration newConfig) {
      super.onConfigurationChanged(newConfig);

      Timber.tag(LC_TAG).d("onConfigurationChanged [%s]", newConfig);

      Timber.tag(LC_TAG).d("onDestroyView");
      for (Plugin component : pluginList) {
         component.onDestroyView();
      }

      Timber.tag(LC_TAG).d("onConfigurationChanged");
      for (Plugin component : pluginList) {
         component.onConfigurationChanged(newConfig);
      }

      Timber.tag(LC_TAG).d("onCreateView");
      for (Plugin component : pluginList) {
         component.onCreateView();
      }
   }

   //###############################################
   // Other
   //###############################################

   @Override
   public boolean onKeyUp(int keyCode, KeyEvent event) {
      Timber.tag(TAG).d("onKeyUp [%s]", event);
      sharedKeyEvent.reset(event);
      for (Plugin plugin : pluginList) {
         sharedKeyEvent.setConsumerCandidate(plugin.getClass());
         plugin.onKeyUp(sharedKeyEvent);
      }
      return sharedKeyEvent.consumed() || super.onKeyUp(keyCode, event);
   }

   @Override
   public boolean onKeyDown(int keyCode, KeyEvent event) {
      Timber.tag(TAG).d("onKeyDown [%s]", event);
      sharedKeyEvent.reset(event);
      for (Plugin plugin : pluginList) {
         plugin.onKeyDown(sharedKeyEvent);
      }
      return sharedKeyEvent.consumed() || super.onKeyDown(keyCode, event);
   }

   @Override
   public boolean dispatchTouchEvent(MotionEvent ev) {
      sharedMotionEvent.reset(ev);
      for (Plugin component : componentTouchList) {
         component.onDispatchTouchEvent(sharedMotionEvent);
      }
      return sharedMotionEvent.consumed() || super.dispatchTouchEvent(ev);
   }

   @Override
   public void onBackPressed() {
      Timber.tag(TAG).d("onBackPressed");
      sharedBackEvent.reset();
      for (Plugin component : componentBackList) {
         component.onBackPressed(sharedBackEvent);
      }
      if (!sharedBackEvent.consumed()) super.onBackPressed();
   }


   //###############################################
   // Props
   //###############################################

   public MainActivityComponent getMainActivityComponent() {
      return mainActivityComponent;
   }

   @Module
   public static class MainActivityModule {

      private final MainActivity mainActivity;

      MainActivityModule(MainActivity mainActivity) {
         this.mainActivity = mainActivity;
      }

      @Provides
      public MainActivity provideMainActivity() {
         return mainActivity;
      }

      @Provides
      public Context provideContext() {
         return mainActivity;
      }

      @Provides
      public Activity provideActivity() {
         return mainActivity;
      }
   }
}
