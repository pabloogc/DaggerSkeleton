package com.bq.daggerskeleton.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bq.daggerskeleton.flux.Store;
import com.bq.daggerskeleton.sample.app.App;

import java.util.Map;
import java.util.Set;

public class FluxUtil {
   @NonNull
   @SuppressWarnings("unchecked")
   public static <T extends Store<?>> T findStore(Context context, Class<T> kind) {
      Map<Class<?>, Store<?>> stores = ((App) context.getApplicationContext()).getAppComponent().stores();
      Store<?> store = stores.get(kind);
      if (store == null) {
         throw new IllegalArgumentException("Store not found");
      }
      return (T) store;
   }
}
