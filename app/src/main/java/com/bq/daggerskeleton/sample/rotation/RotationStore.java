package com.bq.daggerskeleton.sample.rotation;

import android.content.Context;
import android.view.OrientationEventListener;

import com.bq.daggerskeleton.sample.app.App;
import com.bq.daggerskeleton.sample.app.AppScope;
import com.bq.daggerskeleton.flux.Dispatcher;
import com.bq.daggerskeleton.flux.Store;
import com.bq.daggerskeleton.sample.app.LifeCycleAction;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;


@AppScope
public class RotationStore extends Store<RotationState> {

   private final OrientationHandler orientationEventListener;

   @Override protected RotationState initialState() {
      return new RotationState();
   }

   @Inject RotationStore(App app) {

      orientationEventListener = new OrientationHandler(app);

      Dispatcher.subscribe(LifeCycleAction.class, a -> {
         switch (a.event) {
            //Reset all state in both cases so views are easier to implement (no corner cases)
            case ON_CREATE:
               orientationEventListener.reset();
               setState(initialState()); //Reset
               orientationEventListener.enable();
               break;
            case ON_DESTROY:
               orientationEventListener.reset();
               setState(initialState()); //Reset
               orientationEventListener.disable();
               break;
         }
      });

      Dispatcher.subscribe(DeviceRotatedAction.class, a -> {
         RotationState newState = new RotationState(state());
         newState.deviceAccumulatedRotation = a.deviceAccumulatedRotation;
         newState.deviceAbsoluteRotation = newState.deviceAccumulatedRotation;

         while (newState.deviceAbsoluteRotation < 0) {
            newState.deviceAbsoluteRotation += 360;
         }
         newState.deviceAbsoluteRotation = newState.deviceAbsoluteRotation % 360;

         setState(newState);
      });
   }

   private static final class OrientationHandler extends OrientationEventListener {
      private int lastBucketRotation = -1;
      private int lastBucket = 0;
      private int accumulatedRotation = 0;

      public OrientationHandler(Context context) {
         super(context);
      }

      void reset() {
         lastBucketRotation = -1;
         lastBucket = 0;
         accumulatedRotation = 0;
      }

      @Override public void onOrientationChanged(int orientation) {
         if (orientation == ORIENTATION_UNKNOWN) return;
         if (lastBucketRotation == -1) {
            lastBucketRotation = orientation;
            return;
         }
         int bucket = 0;

         if (orientation >= 315 && orientation < 360) bucket = 0;
         else if (orientation >= 0 && orientation < 45) bucket = 0;
         else if (orientation >= 45 && orientation < 135) bucket = 1;
         else if (orientation >= 135 && orientation < 225) bucket = 2;
         else if (orientation >= 225 && orientation < 315) bucket = 3;

         int rotationDiff = Math.abs(orientation - lastBucketRotation);

         if (bucket != lastBucket && rotationDiff > 30) {
            int rotationSteps;
            //Need to hardcode both corner cases
            if (lastBucket == 3 && bucket == 0) rotationSteps = -1;
            else if (lastBucket == 0 && bucket == 3) rotationSteps = 1;
               //This will be 1, -1, 2, -2, depending on direction
            else rotationSteps = lastBucket - bucket;


            accumulatedRotation += rotationSteps * 90;
            lastBucket = bucket;
            lastBucketRotation = orientation;
            Dispatcher.dispatch(new DeviceRotatedAction(accumulatedRotation));
         }
      }
   }

   @Module
   public static class RotationModule {

      @Provides @AppScope @IntoSet
      static Store<?> provideRotationStoreToSet(RotationStore store) {
         return store;
      }
   }
}
