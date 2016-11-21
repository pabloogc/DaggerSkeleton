package com.bq.daggerskeleton.dagger;


import java.lang.reflect.Method;
import java.util.Set;

/**
 * Dagger utility methods to work with the plugin based architecture.
 */
public class DaggerUtil {

   /**
    * Return the single value (usually an implementation) from the set or throw.
    */
   public static <T> T getSingleValue(Set<T> implementations) {
      if (implementations.isEmpty())
         throw new IllegalStateException("There are no implementations");

      if (implementations.size() != 1)
         throw new IllegalStateException("More than one implementation");

      return implementations.iterator().next();
   }

   /**
    * Generic field injection to avoid coupling plugins to their component by type
    * while allowing <code>@Inject</code> fields.
    */
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
