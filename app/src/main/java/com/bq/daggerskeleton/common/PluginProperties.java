package com.bq.daggerskeleton.common;

public final class PluginProperties {

   public static final PluginProperties DEFAULT = new PluginProperties(false, 0, 0, 0);

   public final boolean willHandleTouch;
   public final int lifecyclePriority;
   public final int backPriority;
   public final int touchPriority;

   PluginProperties(boolean willHandleTouch, int lifecyclePriority, int backPriority, int touchPriority) {
      this.willHandleTouch = willHandleTouch;
      this.lifecyclePriority = lifecyclePriority;
      this.backPriority = backPriority;
      this.touchPriority = touchPriority;
   }

   public static final class Builder {

      private boolean willHandleTouch;
      private int lifecyclePriority;
      private int backPriority;
      private int touchPriority;

      public Builder willHandleTouch(boolean willHandleTouch) {
         this.willHandleTouch = willHandleTouch;
         return this;
      }

      public Builder lifecyclePriority(int lifecyclePriority) {
         this.lifecyclePriority = lifecyclePriority;
         return this;
      }

      public Builder backPriority(int backPriority) {
         this.backPriority = backPriority;
         return this;
      }

      public Builder touchPriority(int touchPriority) {
         this.touchPriority = touchPriority;
         return this;
      }

      public PluginProperties build() {
         return new PluginProperties(willHandleTouch, lifecyclePriority, backPriority, touchPriority);
      }
   }
}
