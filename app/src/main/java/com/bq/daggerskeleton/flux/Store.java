package com.bq.daggerskeleton.flux;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bq.daggerskeleton.common.log.LoggerPlugin;

import io.reactivex.Emitter;
import io.reactivex.Flowable;
import io.reactivex.processors.PublishProcessor;

public abstract class Store<S> {

   @Nullable
   private S state;

   @LoggerPlugin.AutoLog
   private final PublishProcessor<S> processor = PublishProcessor.create();

   protected abstract S initialState();

   public Flowable<S> flowable() {
      return processor;
   }

   @NonNull
   public final S state() {
      if (state == null) state = initialState();
      return state;
   }

   protected final void setState(@NonNull S newState) {
      if (newState.equals(state())) return;
      state = newState;
      processor.onNext(state);
   }
}
