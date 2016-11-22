package com.bq.daggerskeleton.sample.hardware;

import com.bq.daggerskeleton.flux.Action;

/**
 * Dispatched when the Android M permissions change.
 */
public class CameraPermissionChangedAction implements Action {

   public final boolean granted;

   public CameraPermissionChangedAction(boolean granted) {
      this.granted = granted;
   }

   @Override public String toString() {
      return "CameraPermissionChangedAction{" +
            "granted=" + granted +
            '}';
   }
}
