package com.bq.daggerskeleton.sample.storage;

import com.bq.daggerskeleton.flux.Action;

/**
 * Action triggered when a media of any kind has been saved to disk.
 * <p>
 * Triggered by {@link StorageStore}.
 */
public class MediaSavedAction implements Action {
   public final Type mediaType;
   public final String newFileUri;

   public MediaSavedAction(Type mediaType, String newFileUri) {
      this.mediaType = mediaType;
      this.newFileUri = newFileUri;
   }

   @Override public String toString() {
      return "MediaSavedAction{" +
            "mediaType=" + mediaType +
            ", newFileUri='" + newFileUri + '\'' +
            '}';
   }

   /**
    * Type of the media that has been saved.
    */
   public enum Type {
      PHOTO,
      VIDEO
   }
}
