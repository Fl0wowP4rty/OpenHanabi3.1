package org.apache.commons.codec.net;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.BitSet;
import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringDecoder;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.binary.StringUtils;

public class URLCodec implements BinaryEncoder, BinaryDecoder, StringEncoder, StringDecoder {
   /** @deprecated */
   @Deprecated
   protected volatile String charset;
   protected static final byte ESCAPE_CHAR = 37;
   /** @deprecated */
   @Deprecated
   protected static final BitSet WWW_FORM_URL;
   private static final BitSet WWW_FORM_URL_SAFE = new BitSet(256);

   public URLCodec() {
      this("UTF-8");
   }

   public URLCodec(String charset) {
      this.charset = charset;
   }

   public static final byte[] encodeUrl(BitSet urlsafe, byte[] bytes) {
      if (bytes == null) {
         return null;
      } else {
         if (urlsafe == null) {
            urlsafe = WWW_FORM_URL_SAFE;
         }

         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         byte[] var3 = bytes;
         int var4 = bytes.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            byte c = var3[var5];
            int b = c;
            if (c < 0) {
               b = 256 + c;
            }

            if (urlsafe.get(b)) {
               if (b == 32) {
                  b = 43;
               }

               buffer.write(b);
            } else {
               buffer.write(37);
               char hex1 = Utils.hexDigit(b >> 4);
               char hex2 = Utils.hexDigit(b);
               buffer.write(hex1);
               buffer.write(hex2);
            }
         }

         return buffer.toByteArray();
      }
   }

   public static final byte[] decodeUrl(byte[] bytes) throws DecoderException {
      if (bytes == null) {
         return null;
      } else {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();

         for(int i = 0; i < bytes.length; ++i) {
            int b = bytes[i];
            if (b == 43) {
               buffer.write(32);
            } else if (b == 37) {
               try {
                  ++i;
                  int u = Utils.digit16(bytes[i]);
                  ++i;
                  int l = Utils.digit16(bytes[i]);
                  buffer.write((char)((u << 4) + l));
               } catch (ArrayIndexOutOfBoundsException var6) {
                  throw new DecoderException("Invalid URL encoding: ", var6);
               }
            } else {
               buffer.write(b);
            }
         }

         return buffer.toByteArray();
      }
   }

   public byte[] encode(byte[] bytes) {
      return encodeUrl(WWW_FORM_URL_SAFE, bytes);
   }

   public byte[] decode(byte[] bytes) throws DecoderException {
      return decodeUrl(bytes);
   }

   public String encode(String str, String charsetName) throws UnsupportedEncodingException {
      return str == null ? null : StringUtils.newStringUsAscii(this.encode(str.getBytes(charsetName)));
   }

   public String encode(String str) throws EncoderException {
      if (str == null) {
         return null;
      } else {
         try {
            return this.encode(str, this.getDefaultCharset());
         } catch (UnsupportedEncodingException var3) {
            throw new EncoderException(var3.getMessage(), var3);
         }
      }
   }

   public String decode(String str, String charsetName) throws DecoderException, UnsupportedEncodingException {
      return str == null ? null : new String(this.decode(StringUtils.getBytesUsAscii(str)), charsetName);
   }

   public String decode(String str) throws DecoderException {
      if (str == null) {
         return null;
      } else {
         try {
            return this.decode(str, this.getDefaultCharset());
         } catch (UnsupportedEncodingException var3) {
            throw new DecoderException(var3.getMessage(), var3);
         }
      }
   }

   public Object encode(Object obj) throws EncoderException {
      if (obj == null) {
         return null;
      } else if (obj instanceof byte[]) {
         return this.encode((byte[])((byte[])obj));
      } else if (obj instanceof String) {
         return this.encode((String)obj);
      } else {
         throw new EncoderException("Objects of type " + obj.getClass().getName() + " cannot be URL encoded");
      }
   }

   public Object decode(Object obj) throws DecoderException {
      if (obj == null) {
         return null;
      } else if (obj instanceof byte[]) {
         return this.decode((byte[])((byte[])obj));
      } else if (obj instanceof String) {
         return this.decode((String)obj);
      } else {
         throw new DecoderException("Objects of type " + obj.getClass().getName() + " cannot be URL decoded");
      }
   }

   public String getDefaultCharset() {
      return this.charset;
   }

   /** @deprecated */
   @Deprecated
   public String getEncoding() {
      return this.charset;
   }

   static {
      int i;
      for(i = 97; i <= 122; ++i) {
         WWW_FORM_URL_SAFE.set(i);
      }

      for(i = 65; i <= 90; ++i) {
         WWW_FORM_URL_SAFE.set(i);
      }

      for(i = 48; i <= 57; ++i) {
         WWW_FORM_URL_SAFE.set(i);
      }

      WWW_FORM_URL_SAFE.set(45);
      WWW_FORM_URL_SAFE.set(95);
      WWW_FORM_URL_SAFE.set(46);
      WWW_FORM_URL_SAFE.set(42);
      WWW_FORM_URL_SAFE.set(32);
      WWW_FORM_URL = (BitSet)WWW_FORM_URL_SAFE.clone();
   }
}
