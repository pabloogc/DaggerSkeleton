package com.bq.daggerskeleton.sample.hardware.session;


import android.hardware.camera2.CameraCaptureSession;
import android.support.annotation.Nullable;
import android.view.Surface;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

/**
 * Session state controlled by {@link SessionStore}.
 */
public class SessionState {

   @NotNull public OutputMode outputMode = OutputMode.PHOTO;
   /**
    * Surface providers are responsible for handling the surface lifecycle, this is just a hook
    * for any future mode (for now, ImageReader surface and MediaRecorder surface).
    */
   @NotNull public WeakReference<Surface> targetSurface = new WeakReference<>(null);
   public CameraCaptureSession session = null;
   @NotNull public Status status = Status.NO_SESSION;
   @Nullable public Throwable error = null;

   public SessionState() {
   }

   public SessionState(SessionState other) {
      this.outputMode = other.outputMode;
      this.targetSurface = other.targetSurface;
      this.session = other.session;
      this.status = other.status;
      this.error = other.error;
   }

   @Override public String toString() {
      return "SessionState{" +
            "outputMode=" + outputMode +
            ", targetSurface=" + targetSurface +
            ", session=" + session +
            ", status=" + status +
            ", error=" + error +
            '}';
   }

   public enum OutputMode {
      PHOTO,
      VIDEO
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
}
