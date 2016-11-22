package com.bq.daggerskeleton.sample.storage;

/**
 * Storage state controlled by {@link StorageStore}.
 */
public class StorageState {

   public boolean canSaveMedia;

   public StorageState() {

   }

   public StorageState(StorageState other) {
      this.canSaveMedia = other.canSaveMedia;
   }

   @Override public String toString() {
      return "StorageState{" +
            "canSaveMedia=" + canSaveMedia +
            '}';
   }
}
