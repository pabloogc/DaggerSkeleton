package com.bq.daggerskeleton.util;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.idling.CountingIdlingResource;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class RxCountingResourceRule implements TestRule {

   private final CountingIdlingResource idlingResource;

   public RxCountingResourceRule(String resourceName) {
      this.idlingResource = new CountingIdlingResource(resourceName, true);
   }

   @Override public Statement apply(Statement base, Description description) {
      return new Statement() {
         @Override public void evaluate() throws Throwable {
            Espresso.registerIdlingResources(idlingResource);
            //Before
            base.evaluate();
            //After
            Espresso.unregisterIdlingResources(idlingResource);
         }
      };
   }

   public void increment() {
      idlingResource.increment();
   }

   public void decrement() {
      idlingResource.decrement();
   }

   public CountingIdlingResource getIdlingResource() {
      return idlingResource;
   }
}
