package com.bq.daggerskeleton.sample.rotation;


import com.bq.daggerskeleton.flux.Action;

/**
 * Device rotation detected, accumulated by time (two full rotations go to 720).
 */
public final class DeviceRotatedAction implements Action {
   public final int deviceAccumulatedRotation;

   public DeviceRotatedAction(int deviceAccumulatedRotation) {
      this.deviceAccumulatedRotation = deviceAccumulatedRotation;
   }

   @Override public String toString() {
      return "DeviceRotatedAction{" +
            "deviceAccumulatedRotation=" + deviceAccumulatedRotation +
            '}';
   }
}
