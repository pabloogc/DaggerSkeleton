package com.bq.daggerskeleton.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Utility checks and object manipulation. Similar to {@link java.util.Objects}
 * but available in api 23.
 */
public class Objs {

   private Objs() {
      //No instances
   }

   /**
    * Assert the object is not null and return it.
    */
   @NonNull
   public static <T> T notNull(T object) {
      return notNull(object, "parameter");
   }


   /**
    * Assert the object is not null and return it.
    */
   @NonNull
   public static <T> T notNull(T object, String name) {
      if (object == null) throw new NullPointerException(name + " is null.");
      return object;
   }

   /**
    * Return the object if it's not null, otherwise return the default value.
    */
   public static <T> T orDefault(@Nullable T object, @NonNull T def) {
      if (object == null) return def;
      return object;
   }

   /**
    * Return the object if it's not null, otherwise return the default value.
    */
   public static <T> T orDefault(@Nullable T object, Factory<T> factory) {
      if (object == null) return factory.get();
      return object;
   }

   @SuppressWarnings("javadoctype")
   public interface Factory<T> {
      /**
       * Actually create the instance.
       */
      T get();
   }
}
