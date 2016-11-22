package com.bq.daggerskeleton.sample.hardware.session;

import android.hardware.camera2.CameraCaptureSession;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bq.daggerskeleton.flux.Action;

@SuppressWarnings("javadoctype")
public class SessionChangedAction implements Action {

   @NonNull public final SessionState.Status status;
   @Nullable public final Throwable error;
   @Nullable public final CameraCaptureSession session;

   public SessionChangedAction(@Nullable CameraCaptureSession session,
                               @NonNull SessionState.Status status) {
      this(session, status, null);
   }

   public SessionChangedAction(@Nullable CameraCaptureSession session,
                               @NonNull SessionState.Status status, Throwable error) {
      this.session = session;
      this.status = status;
      this.error = null;
   }

   @Override public String toString() {
      return "SessionChangedAction{" +
            "status=" + status +
            ", error=" + error +
            ", session=" + session +
            '}';
   }
}
