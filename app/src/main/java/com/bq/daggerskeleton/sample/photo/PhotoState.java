package com.bq.daggerskeleton.sample.photo;

import android.media.ImageReader;

@SuppressWarnings("javadoctype")
public class PhotoState {
   public ImageReader imageReader;
   public Status status = Status.IDLE;

   public PhotoState() {

   }

   public PhotoState(PhotoState other) {
      this.imageReader = other.imageReader;
      this.status = other.status;
   }

   @Override
   public String toString() {
      return "PhotoState{" +
            "imageReader=" + imageReader +
            ", status=" + status +
            '}';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      PhotoState that = (PhotoState) o;

      if (imageReader != null ? !imageReader.equals(that.imageReader) : that.imageReader != null)
         return false;
      return status == that.status;

   }

   @Override
   public int hashCode() {
      int result = imageReader != null ? imageReader.hashCode() : 0;
      result = 31 * result + (status != null ? status.hashCode() : 0);
      return result;
   }

   public enum Status {
      IDLE,
      TAKING,
      SUCCESS,
      ERROR
   }
}
