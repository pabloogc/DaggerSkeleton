package com.bq.daggerskeleton.common;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

public class LoggerState {
   @Nullable public File currentLogFile;
   @NonNull public LoggerStore.FileLogger fileLogger = new LoggerStore.FileLogger();

}
