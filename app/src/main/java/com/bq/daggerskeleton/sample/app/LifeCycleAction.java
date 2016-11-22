package com.bq.daggerskeleton.sample.app;

import com.bq.daggerskeleton.flux.Action;

@SuppressWarnings("javadoctype")
public class LifeCycleAction implements Action {

   public final Event event;

   public LifeCycleAction(Event event) {
      this.event = event;
   }

   @Override public String toString() {
      return "LifeCycleAction{" +
            "event=" + event +
            '}';
   }

   @SuppressWarnings("javadoctype")
   public enum Event {
      ON_CREATE,
      ON_POST_CREATE,
      ON_PLUGINS_CREATED,
      ON_START,
      ON_RESUME,
      ON_PAUSE,
      ON_STOP,
      ON_DESTROY,
      ON_CONFIGURATION_CHANGED,
      ON_DESTROY_DYNAMIC_VIEW,
      ON_CREATE_DYNAMIC_VIEW,
   }

}
