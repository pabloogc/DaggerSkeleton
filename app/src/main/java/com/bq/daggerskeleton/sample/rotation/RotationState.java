package com.bq.daggerskeleton.sample.rotation;


public final class RotationState {

   public int deviceAccumulatedRotation = 0;
   public int deviceAbsoluteRotation = 0;

   public RotationState() {
   }

   public RotationState(RotationState other) {
      this.deviceAccumulatedRotation = other.deviceAccumulatedRotation;
      this.deviceAbsoluteRotation = other.deviceAbsoluteRotation;
   }

   @Override public String toString() {
      return "RotationState{" +
            "deviceAccumulatedRotation=" + deviceAccumulatedRotation +
            ", deviceAbsoluteRotation=" + deviceAbsoluteRotation +
            '}';
   }
}
