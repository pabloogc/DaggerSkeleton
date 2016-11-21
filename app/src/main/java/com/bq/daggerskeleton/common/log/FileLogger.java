package com.bq.daggerskeleton.common.log;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import timber.log.Timber;

import static android.util.Log.DEBUG;
import static android.util.Log.ERROR;
import static android.util.Log.INFO;
import static android.util.Log.WARN;

/**
 * Logger that writes everything reported to {@link Timber} to a file.
 */
public final class FileLogger {

   private static final DateFormat LOG_FILE_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS", Locale.US);
   private final BlockingQueue<LogLine> queue = new ArrayBlockingQueue<>(100);
   private final Pools.SynchronizedPool<LogLine> pool = new Pools.SynchronizedPool<>(20);

   @NonNull private final Thread backgroundThread;
   @NonNull private final File file;
   @Nullable private final Writer writer;

   public FileLogger(@NonNull File file) {
      Writer writer;
      this.file = file;
      this.backgroundThread = new Thread(this::loop);
      try {
         //Not buffered, we want to write on the spot
         writer = new FileWriter(file.getAbsolutePath(), true);
         this.backgroundThread.start();
      } catch (IOException e) {
         writer = null;
         Timber.e(e);
      }
      this.writer = writer;
   }

   @NonNull public File getFile() {
      return file;
   }

   /**
    * Flush the file, this call is required before application dies or the file will be empty.
    */
   public void flush() {
      if (writer != null) {
         try {
            writer.flush();
         } catch (IOException e) {
            Timber.e(e);
         }
      }
   }

   private void loop() {
      while (true) {
         try {
            LogLine logLine = queue.take();
            String line = logLine.format();
            if (writer != null) writer.write(line);
            logLine.clear();
            pool.release(logLine);
         } catch (InterruptedException e) {
            break; //We are done
         } catch (IOException e) {
            Timber.e(e);
            break;
         }
      }
      closeSilently();
   }

   /**
    * Log a line to the file. This method does not block.
    */
   public void log(int priority, String tag, String message, Throwable t) {
      if (t != null) {
         message = getStackTraceString(t);
      }
      enqueueLog(priority, tag, message);
   }


   /**
    * Close the file and exit. This method does not block.
    */
   public void exit() {
      this.backgroundThread.interrupt();
   }

   private void enqueueLog(int level, String tag, String log) {
      LogLine logLine = pool.acquire();
      if (logLine == null) {
         logLine = new LogLine();
      }

      logLine.tag = tag;
      logLine.log = log;
      logLine.level = level;
      logLine.date.setTime(System.currentTimeMillis());

      queue.offer(logLine);
   }

   private void closeSilently() {
      if (writer != null) {
         try {
            writer.flush();
            writer.close();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   /**
    * Transform a stacktrace into a plain string.
    */
   private static String getStackTraceString(Throwable t) {
      // Don't replace this with Log.getStackTraceString() - it hides
      // UnknownHostException, which is not what we want.
      StringWriter sw = new StringWriter(256);
      PrintWriter pw = new PrintWriter(sw, false);
      t.printStackTrace(pw);
      pw.flush();
      return sw.toString();
   }

   /**
    * Utility method to convert a file to a string.
    */
   public static String fileToString(File file) {
      try {
         BufferedReader reader;
         reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
         StringBuilder sb = new StringBuilder();
         String line;
         while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
         }
         reader.close();
         return sb.toString();
      } catch (IOException e) {
         e.printStackTrace();
      }
      return "Error Reading File";
   }

   private static final class LogLine {
      final Date date = new Date();
      int level;
      String log;
      String tag;

      void clear() {
         log = null;
         tag = null;
         date.setTime(0);
         level = 0;
      }

      String format() {
         String levelString;
         switch (level) {
            case DEBUG:
               levelString = "Debug";
               break;
            case INFO:
               levelString = "Info";
               break;
            case WARN:
               levelString = "Warn️";
               break;
            case ERROR:
               levelString = "Error️";
               break;
            default:
               levelString = "Verbose️";
               break;
         }

         return String.format(Locale.US, "[%s] %s/%s: %s\n", LOG_FILE_DATE_FORMAT.format(date), levelString, tag, log);
      }
   }

   @Override public String toString() {
      return "FileLogger{" +
            "file=" + file.getAbsolutePath() +
            '}';
   }

   @Override protected void finalize() throws Throwable {
      super.finalize();
      flush();
   }
}
