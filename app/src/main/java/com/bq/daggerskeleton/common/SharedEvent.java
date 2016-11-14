package com.bq.daggerskeleton.common;


import android.support.compat.BuildConfig;
import android.util.Log;

import timber.log.Timber;

public final class SharedEvent<T> {

   private static final boolean DEBUG_MODE = BuildConfig.DEBUG;

   private final boolean includeTrace;
   private Exception trace;
   private T event;
   private boolean consumed = false;

   public static <T> SharedEvent<T> create() {
      return create(DEBUG_MODE);
   }

   //Not final api
   private static <T> SharedEvent<T> create(boolean includeTrace) {
      return new SharedEvent<>(includeTrace);
   }

   private SharedEvent(boolean includeTrace) {
      this.includeTrace = includeTrace;
   }


   public void reset() {
      reset(null);
   }

   public void reset(T event) {
      this.event = event;
      this.consumed = false;
   }

   public T take() {
      if (consumed()) {
         throw new IllegalStateException(
               "This event was already consumed. " +
                     "See the exception cause for trace call where the original take was made.", trace);
      }

      if (includeTrace) {
         try {
            throw new Exception();
         } catch (Exception trace) {
            //This will keep the trace that led to the event being consumed.
            //Rewinding a trace has a big performance impact, don't use it in production
            this.trace = trace;
         }
      }
      Timber.d("Event Consumed: %s", event);
      consumed = true;
      return event;
   }

   public boolean consumed() {
      return consumed;
   }

   public T peek() {
      return event;
   }

   @Override public String toString() {
      return "SharedEvent{" +
            "event=" + event +
            ", consumed=" + consumed +
            '}';
   }
}
