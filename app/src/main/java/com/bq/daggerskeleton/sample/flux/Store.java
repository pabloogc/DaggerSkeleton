package com.bq.daggerskeleton.sample.flux;

import android.support.annotation.NonNull;

import com.bq.daggerskeleton.common.LoggerPlugin;

import io.reactivex.Flowable;
import io.reactivex.processors.PublishProcessor;

public abstract class Store<S> {

   private S state = initialState();

   @LoggerPlugin.AutoLog
   private final PublishProcessor<S> processor = PublishProcessor.create();

   protected abstract S initialState();

   public Flowable<S> flowable() {
      return processor;
   }

   public S state() {
      return state;
   }

   protected void setState(@NonNull S newState) {
      if (newState.equals(state)) return;
      state = newState;
      processor.onNext(state);
   }
}
