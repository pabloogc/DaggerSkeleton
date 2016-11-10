package com.bq.daggerskeleton.sample.rotation;


import com.bq.daggerskeleton.sample.flux.Action;

public final class DeviceRotatedAction implements Action{
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
