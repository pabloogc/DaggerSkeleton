package com.bq.daggerskeleton.common.log;

import android.support.annotation.Nullable;


@SuppressWarnings("javadoctype")
public class LoggerState {
   @Nullable public final FileLogger fileLogger;

   public LoggerState() {
      this(null);
   }

   public LoggerState(@Nullable FileLogger fileLogger) {
      this.fileLogger = fileLogger;
   }

   @Override public String toString() {
      return "LoggerState{" +
            "fileLogger=" + fileLogger +
            '}';
   }
}
