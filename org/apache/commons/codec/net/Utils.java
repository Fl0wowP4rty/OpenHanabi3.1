package org.apache.commons.codec.net;

import org.apache.commons.codec.DecoderException;

class Utils {
   private static final int RADIX = 16;

   static int digit16(byte b) throws DecoderException {
      int i = Character.digit((char)b, 16);
      if (i == -1) {
         throw new DecoderException("Invalid URL encoding: not a valid digit (radix 16): " + b);
      } else {
         return i;
      }
   }

   static char hexDigit(int b) {
      return Character.toUpperCase(Character.forDigit(b & 15, 16));
   }
}
