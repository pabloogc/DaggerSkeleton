
package com.bq.daggerskeleton.common;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.MotionEvent;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public abstract class SimplePlugin implements Plugin {

   @Inject MainActivity activity;
   private final CompositeDisposable compositeDisposable = new CompositeDisposable();

   public Context getContext() {
      return activity;
   }

   public MainActivity getActivity() {
      return activity;
   }

   public void track(Disposable disposable) {
      compositeDisposable.add(disposable);
   }

   //###############################
   // No-op
   //###############################

   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
   }

   @Override
   public void onCreateView() {

   }

   @Override
   public void onDestroyView() {

   }

   @Override
   public void onPostCreate() {

   }

   @Override
   public void onComponentsReady() {
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
