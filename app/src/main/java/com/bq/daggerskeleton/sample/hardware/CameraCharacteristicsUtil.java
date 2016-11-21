package com.bq.daggerskeleton.sample.hardware;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.support.annotation.Nullable;
import android.util.Size;

import com.bq.daggerskeleton.sample.preview.PreviewUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.hardware.camera2.CameraCharacteristics.LENS_FACING;
import static android.hardware.camera2.CameraCharacteristics.SENSOR_ORIENTATION;
import static android.hardware.camera2.CameraMetadata.LENS_FACING_BACK;
import static android.hardware.camera2.CameraMetadata.LENS_FACING_FRONT;

public class CameraCharacteristicsUtil {

   public static boolean isFrontCamera(CameraCharacteristics cameraCharacteristics) {
      return LENS_FACING_FRONT == cameraCharacteristics.get(LENS_FACING);
   }

   public static boolean isBackCamera(CameraCharacteristics cameraCharacteristics) {
      return LENS_FACING_BACK == cameraCharacteristics.get(LENS_FACING);
   }

   public static int getSensorOrientation(CameraCharacteristics cameraCharacteristics) {
      return cameraCharacteristics.get(SENSOR_ORIENTATION);
   }

   @Nullable
   public static List<Size> getJpegOutputSizes(CameraCharacteristics cameraCharacteristics) {
      // Get JPEG resolutions
      Size[] jpegResolutionsArray = null;

      StreamConfigurationMap streamConfigurationMap = cameraCharacteristics
            .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

      if (streamConfigurationMap != null) {
         jpegResolutionsArray = streamConfigurationMap.getOutputSizes(ImageFormat.JPEG);
      }
      List<Size> jpegResolutions = Arrays.asList(jpegResolutionsArray);

      // Sort by descending area size
      Collections.sort(jpegResolutions, new PreviewUtil.CompareSizesByArea());
      Collections.reverse(jpegResolutions);

      return jpegResolutions;
   }

   @Nullable
   public static List<Size> getMediaRecorderOutputSizes(CameraCharacteristics cameraCharacteristics) {
      // Get JPEG resolutions
      Size[] mediaRecorderOutputSizesArray = null;

      StreamConfigurationMap streamConfigurationMap = cameraCharacteristics
            .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

      if (streamConfigurationMap != null) {
         mediaRecorderOutputSizesArray = streamConfigurationMap.getOutputSizes(MediaRecorder.class);
      }
      List<Size> mediaRecorderOutputSizesList = Arrays.asList(mediaRecorderOutputSizesArray);

      // Sort by descending area size
      Collections.sort(mediaRecorderOutputSizesList, new PreviewUtil.CompareSizesByArea());
      Collections.reverse(mediaRecorderOutputSizesList);

      return mediaRecorderOutputSizesList;
   }

}
