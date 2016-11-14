package com.bq.daggerskeleton.flux;


import android.os.Looper;
import android.support.compat.BuildConfig;

import org.jetbrains.annotations.TestOnly;

import java.util.HashMap;
import java.util.TreeSet;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.PublishProcessor;
import timber.log.Timber;

public class Dispatcher {

   public static final int DEFAULT_PRIORITY = 50;

   private static final HashMap<Class<? extends Action>, TreeSet<Subscription<? extends Action>>> dispatchMap = new HashMap<>();

   private Dispatcher() {
      //No instances
   }

   public static void dispatch(Action action) {
      ensureUiThread();
      Timber.i("Action -> %s", action);
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

   public static <T extends Action> Flowable<T> subscribe(Class<T> actionType) {
      return subscribe(DEFAULT_PRIORITY, actionType);
   }

   public static <T extends Action> Flowable<T> subscribe(int priority, Class<T> actionType) {
      PublishProcessor<T> processor = PublishProcessor.create();
      subscribe(priority, actionType, processor::onNext);
      return processor;
   }

   public static <T extends Action> void subscribe(Class<T> actionType, Consumer<T> consumer) {
      subscribe(DEFAULT_PRIORITY, actionType, consumer);
   }

   public static <T extends Action> void subscribe(int priority, Class<T> actionType, Consumer<T> consumer) {
      ensureUiThread();
      TreeSet<Subscription<? extends Action>> set = dispatchMap.get(actionType);
      if (set == null) {
         set = new TreeSet<>((a, b) -> Integer.compare(a.priority, b.priority));
         dispatchMap.put(actionType, set);
      }
      set.add(new Subscription<>(priority, consumer));
   }

   @TestOnly
   static void clearSubscriptions() {
      dispatchMap.clear();
   }

   private static void ensureUiThread() {
      if (!BuildConfig.DEBUG) return;
      if (Looper.myLooper() != Looper.getMainLooper()) {
         throw new IllegalStateException("Dispatcher is not thread safe " +
               "and can only be accessed from Ui thread.");
      }
   }

   private static final class Subscription<T extends Action> {
      final int priority;
      final Consumer<Action> consumer;

      @SuppressWarnings("unchecked")
      private Subscription(int priority, Consumer<T> consumer) {
         this.priority = priority;
         this.consumer = (Consumer<Action>) consumer;
      }
   }
}
