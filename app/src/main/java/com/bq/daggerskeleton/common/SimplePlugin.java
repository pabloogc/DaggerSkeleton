
package com.bq.daggerskeleton.common;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.MotionEvent;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Empty implementation for a Plugin.
 */
public abstract class SimplePlugin implements Plugin {

   private final CompositeDisposable compositeDisposable = new CompositeDisposable();

   public void track(Disposable disposable) {
      compositeDisposable.add(disposable);
   }


   @Override
   public PluginProperties getProperties() {
      return PluginProperties.DEFAULT;
   }

   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
   }

   @Override
   public void onCreateDynamicView() {

   }

   @Override
   public void onDestroyDynamicView() {

   }

   @Override
   public void onPostCreate() {

   }

   @Override
   public void onComponentsCreated() {
   }

   @Override
   public void onConfigurationChanged(@NonNull Configuration newConfig) {
   }

   @Override
   public void onSaveInstanceState(@NonNull Bundle outState) {
   }

   @Override
   public void onDestroy() {
      compositeDisposable.clear();
   }

   @Override
   public void onStart() {
   }

   @Override
   public void onResume() {
   }

   @Override
   public void onPause() {
   }

   @Override
   public void onStop() {
   }

   @Override
   public void onBackPressed(SharedEvent<Void> ev) {
   }

   @Override
   public void onDispatchTouchEvent(SharedEvent<MotionEvent> ev) {
   }

   @Override
   public void onKeyDown(SharedEvent<KeyEvent> ev) {

   }

   @Override
   public void onKeyUp(SharedEvent<KeyEvent> ev) {

   }
}
