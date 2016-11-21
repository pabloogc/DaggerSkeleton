package com.bq.daggerskeleton.common;


import android.support.compat.BuildConfig;
import android.util.Log;

import timber.log.Timber;

/**
 * Value wrapper that can be consumed once. Useful for reactive streams that may share an action
 * that can't have two side effects (back, touch).
 *
 * @param <T> The wrapped value.
 */
public final class SharedEvent<T> {

   private static final boolean DEBUG_MODE = BuildConfig.DEBUG;

   private final boolean includeTrace;
   private Exception trace;
   private T event;
   private boolean consumed = false;

   /**
    * Reset the event, only the owner should call this method to reuse it.
    */
   private SharedEvent(boolean includeTrace) {
      this.includeTrace = includeTrace;
   }

   /**
    * Create a new {@link SharedEvent} instance with {@link #DEBUG_MODE} set to {@link BuildConfig#DEBUG}.
    */
   public static <T> SharedEvent<T> create() {
      return create(DEBUG_MODE);
   }

   //Not final api
   private static <T> SharedEvent<T> create(boolean includeTrace) {
      return new SharedEvent<>(includeTrace);
   }

   /**
    * Reset the event, only the owner should call this method to reuse it.
    */
   public void reset() {
      reset(null);
   }

   /**
    * Reset the event, only the owner should call this method to reuse it.
    */
   public void reset(T event) {
      this.event = event;
      this.consumed = false;
   }

   /**
    * Take the value and consume the event. Calling this method twice will throw an exception.
    */
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

   /**
    * <code>True</code> if {@link #take()} was called.
    */
   public boolean consumed() {
      return consumed;
   }

   /**
    * Peek the value without consuming.
    */
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
