package com.bq.daggerskeleton.util;

import android.content.Context;

import com.bq.daggerskeleton.flux.Store;
import com.bq.daggerskeleton.sample.app.App;

import java.util.Set;

public class FluxUtil {
   @SuppressWarnings("unchecked")
   public static <T extends Store<?>> T findStore(Context context, Class<T> kind) {
      Set<Store<?>> stores = ((App) context.getApplicationContext()).getAppComponent().stores();
      for (Store<?> store : stores) {
         if (kind.isAssignableFrom(store.getClass())) return (T) store;
      }
      throw new IllegalArgumentException("Store not found");
   }
}
