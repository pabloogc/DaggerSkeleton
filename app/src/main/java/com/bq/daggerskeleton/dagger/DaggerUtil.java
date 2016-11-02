package com.bq.daggerskeleton.dagger;


import android.annotation.SuppressLint;

import java.lang.reflect.Method;
import java.util.Set;

public class DaggerUtil {

   public static <T> T getSingleValue(Set<T> implementations) {
      if (implementations.isEmpty())
         throw new IllegalStateException("There are no implementations");

      if (implementations.size() != 1)
         throw new IllegalStateException("More than one implementation");

      return implementations.iterator().next();
   }

   public static void doInject(Object target, Object component) {
      try {
         final Method injectMethod = component.getClass().getMethod("inject", target.getClass());
         injectMethod.invoke(component, target);
      } catch (NoSuchMethodException e) {
         throw new UnsupportedOperationException(
               "No injection point for: " + target.getClass()
                     + " in: " + component.getClass()
                     + ". Expected a method in the component with signature: " +
                     "void inject(" + target.getClass() + ");");
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }
}
