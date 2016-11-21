package com.bq.daggerskeleton.flux;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bq.daggerskeleton.common.log.LoggerPlugin;

import io.reactivex.Flowable;
import io.reactivex.processors.PublishProcessor;

/**
 * Generic store that exposes its state as a {@link Flowable} and emits change events
 * when {@link #setState(Object)} is called.
 *
 * @param <S> The state type.
 */
public abstract class Store<S> {

   @Nullable
   private S state;

   @LoggerPlugin.AutoLog
   private final PublishProcessor<S> processor = PublishProcessor.create();

   protected abstract S initialState();

   /**
    * Observable state.
    */
   public Flowable<S> flowable() {
      return processor;
   }

   /**
    * Current store state. If state is <code>null</code> {@link #initialState()} is called.
    */
   @NonNull
   public final S state() {
      if (state == null) {
         setState(initialState());
      }
      return state;
   }

   protected final void setState(@NonNull S newState) {
      if (newState.equals(state())) return;
      state = newState;
      processor.onNext(state);
   }
}
