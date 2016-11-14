package com.bq.daggerskeleton.common.log;

import com.bq.daggerskeleton.flux.Dispatcher;
import com.bq.daggerskeleton.flux.InitAction;
import com.bq.daggerskeleton.flux.Store;
import com.bq.daggerskeleton.sample.app.App;
import com.bq.daggerskeleton.sample.app.AppScope;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.content.ContentValues.TAG;
import static android.util.Log.d;
import static android.util.Log.e;

public class LoggerStore extends Store<LoggerState> {

   private static final String LOG_FOLDER = "__camera_log";
   private static final long LOG_FILES_MAX_AGE = 0; //Delete for every session
   private static final DateFormat FILE_NAME_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);

   private final App app;

   @Override protected LoggerState initialState() {
      return new LoggerState(null);
   }

   @Inject LoggerStore(App app, Lazy<Set<Store<?>>> stores) {
      this.app = app;
      Timber.plant(new FileLoggerTree());

      createLogFile()
            //.concatWith(deleteOldLogs(LOG_FILES_MAX_AGE))
            .subscribeOn(Schedulers.io())
            .subscribe();

      Dispatcher.subscribe(InitAction.class)
            .observeOn(Schedulers.io())
            .subscribe(a -> {
               for (Store<?> store : stores.get()) {
                  subscribeToObservableUnsafe(store.flowable(), store.getClass().getSimpleName(), "State");
               }
            });
   }

   private Completable createLogFile() {
      return Completable.create(s -> {
         File cacheDir = app.getCacheDir();
         File logRootDirectory = new File(cacheDir.getAbsolutePath(), LOG_FOLDER);
         if (!logRootDirectory.exists()) {
            if (!logRootDirectory.mkdir()) {
               e(TAG, "Unable to create log directory, nothing will be written on disk");
               s.onError(new SecurityException());
               return;
            }
         }
         FileLogger oldFileLogger = state().fileLogger;
         if (oldFileLogger != null) {
            oldFileLogger.exit();
         }

         String logFileName = String.format("%s-%s.log", "camera", FILE_NAME_DATE_FORMAT.format(new Date()));
         File logFile = new File(logRootDirectory, logFileName);
         d(TAG, "New session, logs will be stored in: " + logFile.getAbsolutePath());
         LoggerState newState = new LoggerState(new FileLogger(logFile));
         setState(newState);
         s.onComplete();
      });
   }

   /**
    * Delete any log files created under {@link #LOG_FOLDER} older that <code>maxAge</code> in ms.
    * <p>
    * Current log file wont be deleted.
    */
   private Completable deleteOldLogs(long maxAge) {
      File logRootDirectory = new File(app.getCacheDir().getAbsolutePath(), LOG_FOLDER);
      return Completable.create(e -> {
         if (!logRootDirectory.exists()) return;
         int deleted = 0;
         final File[] files = logRootDirectory.listFiles();
         if (files != null) {
            for (File file : files) {
               long lastModified = System.currentTimeMillis() - file.lastModified();
               if (lastModified > maxAge) {
                  FileLogger fileLogger = state().fileLogger;
                  File logFile = fileLogger != null ? fileLogger.getFile() : null;
                  boolean isCurrentLogFile = logFile != null &&
                        file.getAbsolutePath().equals(logFile.getAbsolutePath());
                  if (!isCurrentLogFile && file.delete()) deleted++;
               }
            }
         }
         Timber.v("Deleted %d old log files", deleted);
         e.onComplete();
      });
   }

   @SuppressWarnings("unchecked")
   public static Disposable subscribeToObservableUnsafe(Object observable, String tag, String linePrefix) {

      Consumer consumer = value -> Timber.tag(tag).i("%s <- %s", linePrefix, value);
      Consumer errorConsumer = value -> Timber.tag(tag).e("%s <- %s", linePrefix, value);

      Disposable disposable = null;
      if (observable instanceof Observable) {
         disposable = ((Observable) observable).observeOn(Schedulers.io()).subscribe(consumer, errorConsumer);
      } else if (observable instanceof Flowable) {
         disposable = ((Flowable) observable).observeOn(Schedulers.io()).subscribe(consumer, errorConsumer);
      } else if (observable instanceof Single) {
         disposable = ((Single) observable).observeOn(Schedulers.io()).subscribe(consumer, errorConsumer);
      } else if (observable instanceof Maybe) {
         disposable = ((Maybe) observable).observeOn(Schedulers.io()).subscribe(consumer, errorConsumer);
      }

      return disposable;
   }

   @Module
   public static abstract class LoggerModule {
      @Provides @AppScope @IntoSet
      static Store<?> provideLoggerPlugin(LoggerStore store) {
         return store;
      }
   }

   private final class FileLoggerTree extends Timber.Tree {
      @Override protected void log(int priority, String tag, String message, Throwable t) {
         FileLogger fileLogger = state().fileLogger;
         if (fileLogger != null) {
            fileLogger.log(priority, tag, message, t);
         }
      }
   }
}
