package com.bq.daggerskeleton.sample.photo;

import com.bq.daggerskeleton.flux.Action;

@SuppressWarnings("javadoctype")
public class PhotoBytesCapturedAction implements Action {
   public final byte[] bytes;

   public PhotoBytesCapturedAction(byte[] bytes) {
      this.bytes = bytes;
   }

   @Override
   public String toString() {
      return "PhotoBytesCapturedAction{" +
            "bytes=[" + bytes.length + "]" +
            '}';
   }
}
