package com.bq.daggerskeleton.common;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.MotionEvent;


/**
 * Base interface for al plugins loaded in the Activity using the component.
 * The interface mirrors Activity callbacks.
 */
public interface Plugin {

   /**
    * Return plugin runtime information used by the activity to manage callbacks. This method
    * is only called once.
    */
   PluginProperties getProperties();

   /**
    * {@link Activity#onCreate(Bundle)}.
    */
   void onCreate(@Nullable Bundle savedInstanceState);

   /**
    * Called after {@link #onCreate(Bundle)} or during a configuration change.
    * Most components can ignore this method if they ignore configuration changes and
    * do every logic during {@link #onCreate(Bundle)}
    */
   void onCreateDynamicView();

   /**
    * {@link Activity#onPostCreate(Bundle)}.
    */
   void onPostCreate();

   /**
    * All the components have completed their onCreate method, it's safe to reference external views.
    */
   void onComponentsCreated();

   /**
    * {@link Activity#onSaveInstanceState(Bundle)}.
    * <p>
    * Every plugin has its own bundle to avoid key collisions.
    */
   void onSaveInstanceState(@NonNull Bundle outState);


   /**
    * {@link Activity#onDestroy()}.
    */
   void onDestroy();


   /**
    * {@link Activity#onStart()}.
    */
   void onStart();

   /**
    * {@link Activity#onResume()}.
    */
   void onResume();

   /**
    * {@link Activity#onPause()}.
    */
   void onPause();

   /**
    * {@link Activity#onStop()}.
    */
   void onStop();

   /**
    * {@link Activity#onBackPressed()}.
    */
   void onBackPressed(SharedEvent<Void> ev);

   /**
    * {@link Activity#dispatchTouchEvent(MotionEvent)}.
    */
   void onDispatchTouchEvent(SharedEvent<MotionEvent> ev);

   /**
    * {@link Activity#onKeyDown(int, KeyEvent)}.
    */
   void onKeyDown(SharedEvent<KeyEvent> ev);

   /**
    * {@link Activity#onKeyUp(int, KeyEvent)}.
    */
   void onKeyUp(SharedEvent<KeyEvent> ev);

   /**
    * {@link Activity#onConfigurationChanged(Configuration)}.
    */
   void onConfigurationChanged(@NonNull Configuration newConfig);

   /**
    * Called during {@link #onConfigurationChanged(Configuration)}, this call is always
    * followed by a {@link #onCreateDynamicView()}.
    */
   void onDestroyDynamicView();
}
