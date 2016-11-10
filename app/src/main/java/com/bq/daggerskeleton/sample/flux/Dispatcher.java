package com.bq.daggerskeleton.sample.flux;


import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.compat.BuildConfig;
import android.util.SparseArray;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.TreeSet;

import io.reactivex.functions.Consumer;
import io.reactivex.processors.PublishProcessor;
import timber.log.Timber;

public class Dispatcher {

   public static final int DEFAULT_PRIORITY = 50;

   private static final Dispatcher instance = new Dispatcher();
   private static final HashMap<Class<? extends Action>, TreeSet<Subscription<? extends Action>>> dispatchMap = new HashMap<>();

   public static void dispatch(Action action) {
      ensureUiThread();
      Timber.v("Action -> %s", action);
      TreeSet<Subscription<? extends Action>> set = dispatchMap.get(action.getClass());
      if (set != null) {
         for (Subscription<? extends Action> subscription : set) {
            try {
               subscription.consumer.accept(action);
            } catch (Exception e) {
               Timber.e(e);
               throw new RuntimeException(e);
            }
         }
      }
   }

   public static <T extends Action> void subscribe(Class<T> actionType, Consumer<T> consumer) {
      subscribe(DEFAULT_PRIORITY, actionType, consumer);
   }

   public static <T extends Action> void subscribe(int priority, Class<T> actionType, Consumer<T> consumer) {
      TreeSet<Subscription<? extends Action>> set = dispatchMap.get(actionType);
      if (set == null) {
         set = new TreeSet<>((a, b) -> Integer.compare(a.priority, b.priority));
         dispatchMap.put(actionType, set);
      }
      set.add(new Subscription<>(priority, consumer));
   }

   private static void ensureUiThread() {
      if (!BuildConfig.DEBUG) return; //Not needed in production
      if (Looper.myLooper() != Looper.getMainLooper()) {
         throw new IllegalStateException("Can't dispatch actions from background thread");
      }
   }

   private static final class Subscription<T extends Action> implements Comparable<Subscription<? extends Action>> {
      final int priority;
      final Consumer<Action> consumer;

      @SuppressWarnings("unchecked")
      private Subscription(int priority, Consumer<T> consumer) {
         this.priority = priority;
         this.consumer = (Consumer<Action>) consumer;
      }


      @Override public int compareTo(@NonNull Subscription<? extends Action> other) {
         return Integer.compare(this.priority, other.priority);
      }
   }
}
