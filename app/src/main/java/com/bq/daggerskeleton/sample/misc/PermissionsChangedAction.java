package com.bq.daggerskeleton.sample.misc;

import com.bq.daggerskeleton.flux.Action;

/**
 * Dispatched when the Android M permissions change.
 */
public class PermissionsChangedAction implements Action {

   public final boolean granted;

   public PermissionsChangedAction(boolean granted) {
      this.granted = granted;
   }

   @Override public String toString() {
      return "CameraPermissionChangedAction{" +
            "granted=" + granted +
            '}';
   }
}
