package com.bq.daggerskeleton.flux;


import android.os.Handler;
import android.os.Looper;

import org.jetbrains.annotations.TestOnly;

import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.PublishProcessor;
import timber.log.Timber;

public class Dispatcher {

   public static final int VERY_HIGH_PRIORITY = 5;
   public static final int HIGH_PRIORITY = 25;
   public static final int DEFAULT_PRIORITY = 50;
   public static final int LOW_PRIORITY = 75;
   public static final int VERY_LOW_PRIORITY = 100;


   private static int subscriptionCounter = 0;

   private static final HashMap<Class<? extends Action>, Collection<Subscription<? extends Action>>> dispatchMap = new HashMap<>();
   private static final Handler uiHandler = new Handler();
   private static final long startTime = System.currentTimeMillis();
   private static long lastActionTime = System.currentTimeMillis();

   private Dispatcher() {
      //No instances
   }

   /**
    * Dispatch the given <code>action</code> to all subscribers. Subscribers are notified in
    * order based on their priority (lower number -> higher priority).
    */
   public static void dispatch(Action action) {
      ensureUiThread();
      dispatchUnsafe(action);
   }

   /**
    * Same as dispatch, but without concurrency checks.
    */
   static void dispatchUnsafe(Action action) {
      long now = System.currentTimeMillis();
      long diff = now - lastActionTime;
      Timber.i("Action - %5d [%3d] -> %s", now - startTime, (diff > 999 ? 999 : diff), action);
      Collection<Subscription<? extends Action>> subscriptions = dispatchMap.get(action.getClass());
      if (subscriptions != null) {
         for (Subscription<? extends Action> subscription : subscriptions) {
            try {
               subscription.consumer.accept(action);
            } catch (Exception e) {
               Timber.e(e);
               throw new RuntimeException(e);
            }
         }
      }
      lastActionTime = now;
   }

   /**
    * Same as {@link #dispatch(Action)}, but the action is posted on Ui thread.
    */
   public static void dispatchOnUi(Action action) {
      uiHandler.post(() -> dispatch(action));
   }

   /**
    * Rx subscription with default priority.
    */
   public static <T extends Action> Flowable<T> subscribe(Class<T> actionType) {
      return subscribe(DEFAULT_PRIORITY, actionType);
   }

   /**
    * Rx subscription.
    */
   public static <T extends Action> Flowable<T> subscribe(int priority, Class<T> actionType) {
      ensureUiThread();
      return subscribeUnsafe(priority, actionType);
   }

   /**
    * Callback subscription with default priority.
    */
   public static <T extends Action> void subscribe(Class<T> actionType, Consumer<T> consumer) {
      subscribe(DEFAULT_PRIORITY, actionType, consumer);
   }

   /**
    * Callback subscription.
    */
   public static <T extends Action> void subscribe(int priority, Class<T> actionType, Consumer<T> consumer) {
      ensureUiThread();
      subscribeUnsafe(priority, actionType, consumer);
   }

   /**
    * Rx subscription without Ui check.
    */
   static <T extends Action> Flowable<T> subscribeUnsafe(int priority, Class<T> actionType) {
      PublishProcessor<T> processor = PublishProcessor.create();
      subscribeUnsafe(priority, actionType, processor::onNext);
      return processor;
   }

   /**
    * Callback subscription without Ui check.
    */
   static <T extends Action> void subscribeUnsafe(int priority, Class<T> actionType, Consumer<T> consumer) {
      Collection<Subscription<? extends Action>> subscriptions = dispatchMap.get(actionType);
      if (subscriptions == null) {
         subscriptions = new TreeSet<>((a, b) -> {
            int p = Integer.compare(a.priority, b.priority);
            return p != 0 ? p : Integer.compare(a.order, b.order);
         });
         dispatchMap.put(actionType, subscriptions);
      }
      subscriptions.add(new Subscription<>(subscriptionCounter, priority, consumer));
      subscriptionCounter++;
   }

   @TestOnly
   static void clearSubscriptions() {
      dispatchMap.clear();
   }

   private static void ensureUiThread() {
      if (Looper.myLooper() != Looper.getMainLooper()) {
         throw new IllegalStateException("Dispatcher is not thread safe " +
               "and can only be accessed from Ui thread.");
      }
   }

   private static final class Subscription<T extends Action> {
      final int order;
      final int priority;
      final Consumer<Action> consumer;

      @SuppressWarnings("unchecked")
      private Subscription(int order, int priority, Consumer<T> consumer) {
         this.priority = priority;
         this.consumer = (Consumer<Action>) consumer;
         this.order = order;
      }
   }
}
