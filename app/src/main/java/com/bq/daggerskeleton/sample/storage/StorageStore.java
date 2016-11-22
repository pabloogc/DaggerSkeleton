package com.bq.daggerskeleton.sample.storage;

import android.app.Application;
import android.content.ContentResolver;
import android.os.Environment;
import android.provider.MediaStore;

import com.bq.daggerskeleton.flux.Dispatcher;
import com.bq.daggerskeleton.flux.Store;
import com.bq.daggerskeleton.sample.app.AppScope;
import com.bq.daggerskeleton.sample.misc.PermissionsChangedAction;
import com.bq.daggerskeleton.sample.photo.PhotoAvailableAction;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Store that handles saving data of any kind (mediaType, video, etc...) to internal storage. For now it only saves to internal memory.
 */
@AppScope
public class StorageStore extends Store<StorageState> {

   private static final SimpleDateFormat FILE_NAME_DATE_FORMAT =
         new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.getDefault());

   private static final String IMAGE_FILE_FORMAT = "IMG_%s.jpg";
   private static final String VIDEO_FILE_FORMAT = "VID_%s.jpg";

   // Internal storage folder to save media to
   private final File internalStoragePublicCameraDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
   private final ContentResolver contentResolver;

   @Inject
   public StorageStore(Application application) {
      this.contentResolver = application.getContentResolver();

      Dispatcher.subscribe(PermissionsChangedAction.class, action -> {
         StorageState newState = new StorageState(state());
         newState.canSaveMedia = action.granted;
         setState(newState);
      });

      Dispatcher.subscribe(PhotoAvailableAction.class, action -> tryToSaveCapture(action.bytes));
   }

   private void tryToSaveCapture(byte[] bytes) {
      if (!state().canSaveMedia) return;

      Single.create(new SingleOnSubscribe<String>() {
         @Override
         public void subscribe(SingleEmitter<String> e) throws Exception {
            try {
               String fileUri = storeImage(bytes);
               e.onSuccess(fileUri);
            } catch (Exception ex) {
               e.onError(ex);
            }
         }
      })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(s -> {
               Dispatcher.dispatch(new MediaSavedAction(MediaSavedAction.Type.PHOTO, s));
            });
   }

   @SuppressWarnings("ResultOfMethodCallIgnored")
   private String storeImage(byte[] bytes) throws IOException {
      File outputFile = getImageOutputFile();
      // Sanity check: ensure that the folders up to the saved file's parent folder are created before
      // attempting to write the file to disk
      outputFile.getParentFile().mkdirs();

      saveRawImageToDisk(bytes, outputFile);
      return addImageToMediaStore(outputFile);
   }

   private File getImageOutputFile() {
      String date = FILE_NAME_DATE_FORMAT.format(new Date());
      File outputFile = new File(internalStoragePublicCameraDirectory, String.format(IMAGE_FILE_FORMAT, date));

      return outputFile;
   }

   private void saveRawImageToDisk(byte[] data, File output) throws IOException {
      try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(output))) {
         bos.write(data);
      }
   }

   private String addImageToMediaStore(File fileToAdd) {
      try {
         return MediaStore.Images.Media.insertImage(contentResolver, fileToAdd.getAbsolutePath(), fileToAdd.getName(), null);
      } catch (FileNotFoundException e) {
         Timber.e(e, "Could not add file %s to MediaStore", fileToAdd.getAbsoluteFile());
         return null;
      }
   }


   @Module
   @SuppressWarnings("javadoctype")
   public static class StorageModule {
      @Provides @AppScope @IntoMap @ClassKey(StorageStore.class)
      static Store<?> provideStorageStoreToMap(StorageStore store) {
         return store;
      }
   }
}
