package com.bq.daggerskeleton.sample.photo;

import com.bq.daggerskeleton.flux.Action;

/**
 * Action triggered when the {@link PhotoStore}'s ImageReader reports that an image data is already available.
 * The action contains the captured image bytes.
 * <p>
 * Triggered by {@link PhotoStore}.
 */
public class PhotoAvailableAction implements Action {
   public final byte[] bytes;

   public PhotoAvailableAction(byte[] bytes) {
      this.bytes = bytes;
   }

   @Override public String toString() {
      return "PhotoAvailableAction{" +
            "bytes=" + bytes.length +
            '}';
   }
}
