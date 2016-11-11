package com.bq.daggerskeleton.sample.hardware;

import com.bq.daggerskeleton.flux.Action;

public class CameraPermissionChanged implements Action {

   public final boolean granted;

   public CameraPermissionChanged(boolean granted) {
      this.granted = granted;
   }

   @Override public String toString() {
      return "CameraPermissionChanged{" +
            "granted=" + granted +
            '}';
   }
}
