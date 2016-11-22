package com.bq.daggerskeleton.sample.hardware;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.util.Size;

import com.bq.daggerskeleton.sample.preview.PreviewUtil;
import com.bq.daggerskeleton.util.Objs;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.hardware.camera2.CameraCharacteristics.LENS_FACING;
import static android.hardware.camera2.CameraCharacteristics.SENSOR_ORIENTATION;
import static android.hardware.camera2.CameraMetadata.LENS_FACING_BACK;
import static android.hardware.camera2.CameraMetadata.LENS_FACING_FRONT;

/**
 * Utility wraps for some common camera information.
 */
public class CameraCharacteristicsUtil {

   /**
    * Check whether the camera is the frontal one, defaults to <code>false</code> if information is
    * not available.
    */
   public static boolean isFrontCamera(@NonNull CameraCharacteristics cameraCharacteristics) {
      return LENS_FACING_FRONT == Objs.orDefault(cameraCharacteristics.get(LENS_FACING), -1);
   }

   /**
    * Check whether the camera is the back one, defaults to <code>true</code> if information is
    * not available.
    */
   public static boolean isBackCamera(@NonNull CameraCharacteristics cameraCharacteristics) {
      return LENS_FACING_BACK == Objs.orDefault(cameraCharacteristics.get(LENS_FACING), LENS_FACING_BACK);
   }

   /**
    * Checks the camera sensor relative rotation, defaults to 0 if information is not available.
    */
   public static int getSensorOrientation(@NonNull CameraCharacteristics cameraCharacteristics) {
      return Objs.orDefault(cameraCharacteristics.get(SENSOR_ORIENTATION), 0);
   }

   /**
    * Output jpeg sizes ordered by area, the first value will be native camera resolution.
    * List will contain a default 640x480 (4:3) if no information is available.
    */
   @NonNull
   public static List<Size> getJpegOutputSizes(CameraCharacteristics cameraCharacteristics) {
      return getOutputSizes(cameraCharacteristics, ImageFormat.JPEG);
   }


   /**
    * Output MediaRecorder sizes ordered by area, the first value will be native camera resolution.
    * List will contain a default 640x480 (4:3) if no information is available.
    */
   @NonNull
   public static List<Size> getMediaRecorderOutputSizes(CameraCharacteristics cameraCharacteristics) {
      return getOutputSizes(cameraCharacteristics, MediaRecorder.class);
   }

   private static List<Size> getOutputSizes(CameraCharacteristics cameraCharacteristics, Object kind) {
      StreamConfigurationMap streamConfigurationMap = cameraCharacteristics
            .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
      if (streamConfigurationMap != null) {
         Size[] resolutionsArray;
         //The parameter is either a integer (JPEG) or a class (MediaRecorder.class)
         if (kind.getClass() == Integer.TYPE) {
            resolutionsArray = streamConfigurationMap.getOutputSizes((Integer) kind);
         } else {
            resolutionsArray = streamConfigurationMap.getOutputSizes((Class) kind);
         }
         List<Size> jpegResolutions = Arrays.asList(resolutionsArray);

         // Sort by descending area size
         Collections.sort(jpegResolutions, new PreviewUtil.CompareSizesByArea());
         Collections.reverse(jpegResolutions);
         return jpegResolutions;
      } else {
         return Collections.singletonList(new Size(640, 480));
      }
   }
}
