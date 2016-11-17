package com.bq.daggerskeleton.sample.hardware.session;


import android.hardware.camera2.CameraCaptureSession;
import android.support.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

public class SessionState {

   public CameraCaptureSession session = null;
   @NotNull public Status status = Status.NO_SESSION;
   @Nullable public Throwable error = null;

   public SessionState() {
   }

   public SessionState(SessionState other) {
      this.session = other.session;
      this.status = other.status;
      this.error = other.error;
   }

   public enum Status {
      NO_SESSION, READY, OPENING, ERROR;

      public boolean isTerminal() {
         return this == READY || this == ERROR;
      }

      public boolean isReadyOrOpening() {
         return this == READY || this == OPENING;
      }
   }

   @Override public String toString() {
      return "SessionState{" +
            "session=" + session +
            ", status=" + status +
            ", error=" + error +
            '}';
   }
}
