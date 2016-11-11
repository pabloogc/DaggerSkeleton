package com.bq.daggerskeleton.flux;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;


public class DispatcherTest {

   @Before
   public void before() {
      Dispatcher.clearSubscriptions();
   }

   @After
   public void after() {
      Dispatcher.clearSubscriptions();
   }

   @Test
   public void testSubscription() throws Exception {
      ArrayList<String> callOrder = new ArrayList<>();

      Consumer<DummyAction> c1 = a -> callOrder.add("a");
      Consumer<DummyAction> c2 = a -> callOrder.add("b");
      Consumer<DummyAction> c3 = a -> callOrder.add("c");
      Consumer<DummyAction> c4 = a -> callOrder.add("d");

      Dispatcher.subscribe(1, DummyAction.class, c1);
      Dispatcher.subscribe(2, DummyAction.class, c2);
      Dispatcher.subscribe(3, DummyAction.class).subscribe(c3);
      Dispatcher.subscribe(4, DummyAction.class).subscribe(c4);

      Dispatcher.dispatch(new DummyAction());

      String[] expectedCallOrder = {"a", "b", "c", "d"};
      Assert.assertArrayEquals(expectedCallOrder, callOrder.toArray());
   }

   private static final class DummyAction implements Action {

   }
}