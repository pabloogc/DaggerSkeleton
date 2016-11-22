package com.bq.daggerskeleton.sample.photo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bq.daggerskeleton.flux.Action;

/**
 * Action triggered when a mediaType status change is performed.
 * In case of error, this action will also include the error exception.
 * <p>
 * Triggered by {@link PhotoStore}.
 */
public class PhotoStatusChangedAction implements Action {
   @NonNull PhotoState.Status status;
   @Nullable Exception e;

   public PhotoStatusChangedAction(PhotoState.Status status) {
      this(status, null);
   }

   public PhotoStatusChangedAction(PhotoState.Status status, Exception e) {
      this.status = status;
      this.e = e;
   }

   @Override public String toString() {
      return "PhotoStatusChangedAction{" +
            "status=" + status +
            ", e=" + e +
            '}';
   }
}
