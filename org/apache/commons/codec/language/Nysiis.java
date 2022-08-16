package org.apache.commons.codec.language;

import java.util.regex.Pattern;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

public class Nysiis implements StringEncoder {
   private static final char[] CHARS_A = new char[]{'A'};
   private static final char[] CHARS_AF = new char[]{'A', 'F'};
   private static final char[] CHARS_C = new char[]{'C'};
   private static final char[] CHARS_FF = new char[]{'F', 'F'};
   private static final char[] CHARS_G = new char[]{'G'};
   private static final char[] CHARS_N = new char[]{'N'};
   private static final char[] CHARS_NN = new char[]{'N', 'N'};
   private static final char[] CHARS_S = new char[]{'S'};
   private static final char[] CHARS_SSS = new char[]{'S', 'S', 'S'};
   private static final Pattern PAT_MAC = Pattern.compile("^MAC");
   private static final Pattern PAT_KN = Pattern.compile("^KN");
   private static final Pattern PAT_K = Pattern.compile("^K");
   private static final Pattern PAT_PH_PF = Pattern.compile("^(PH|PF)");
   private static final Pattern PAT_SCH = Pattern.compile("^SCH");
   private static final Pattern PAT_EE_IE = Pattern.compile("(EE|IE)$");
   private static final Pattern PAT_DT_ETC = Pattern.compile("(DT|RT|RD|NT|ND)$");
   private static final char SPACE = ' ';
   private static final int TRUE_LENGTH = 6;
   private final boolean strict;

   private static boolean isVowel(char c) {
      return c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U';
   }

   private static char[] transcodeRemaining(char prev, char curr, char next, char aNext) {
      if (curr == 'E' && next == 'V') {
         return CHARS_AF;
      } else if (isVowel(curr)) {
         return CHARS_A;
      } else if (curr == 'Q') {
         return CHARS_G;
      } else if (curr == 'Z') {
         return CHARS_S;
      } else if (curr == 'M') {
         return CHARS_N;
      } else if (curr == 'K') {
         return next == 'N' ? CHARS_NN : CHARS_C;
      } else if (curr == 'S' && next == 'C' && aNext == 'H') {
         return CHARS_SSS;
      } else if (curr == 'P' && next == 'H') {
         return CHARS_FF;
      } else if (curr == 'H' && (!isVowel(prev) || !isVowel(next))) {
         return new char[]{prev};
      } else {
         return curr == 'W' && isVowel(prev) ? new char[]{prev} : new char[]{curr};
      }
   }

   public Nysiis() {
      this(true);
   }

   public Nysiis(boolean strict) {
      this.strict = strict;
   }

   public Object encode(Object obj) throws EncoderException {
      if (!(obj instanceof String)) {
         throw new EncoderException("Parameter supplied to Nysiis encode is not of type java.lang.String");
      } else {
         return this.nysiis((String)obj);
      }
   }

   public String encode(String str) {
      return this.nysiis(str);
   }

   public boolean isStrict() {
      return this.strict;
   }

   public String nysiis(String str) {
      if (str == null) {
         return null;
      } else {
         str = SoundexUtils.clean(str);
         if (str.length() == 0) {
            return str;
         } else {
            str = PAT_MAC.matcher(str).replaceFirst("MCC");
            str = PAT_KN.matcher(str).replaceFirst("NN");
            str = PAT_K.matcher(str).replaceFirst("C");
            str = PAT_PH_PF.matcher(str).replaceFirst("FF");
            str = PAT_SCH.matcher(str).replaceFirst("SSS");
            str = PAT_EE_IE.matcher(str).replaceFirst("Y");
            str = PAT_DT_ETC.matcher(str).replaceFirst("D");
            StringBuilder key = new StringBuilder(str.length());
            key.append(str.charAt(0));
            char[] chars = str.toCharArray();
            int len = chars.length;

            char last2Char;
            for(int i = 1; i < len; ++i) {
               last2Char = i < len - 1 ? chars[i + 1] : 32;
               char aNext = i < len - 2 ? chars[i + 2] : 32;
               char[] transcoded = transcodeRemaining(chars[i - 1], chars[i], last2Char, aNext);
               System.arraycopy(transcoded, 0, chars, i, transcoded.length);
               if (chars[i] != chars[i - 1]) {
                  key.append(chars[i]);
               }
            }

            if (key.length() > 1) {
               char lastChar = key.charAt(key.length() - 1);
               if (lastChar == 'S') {
                  key.deleteCharAt(key.length() - 1);
                  lastChar = key.charAt(key.length() - 1);
               }

               if (key.length() > 2) {
                  last2Char = key.charAt(key.length() - 2);
                  if (last2Char == 'A' && lastChar == 'Y') {
                     key.deleteCharAt(key.length() - 2);
                  }
               }

               if (lastChar == 'A') {
                  key.deleteCharAt(key.length() - 1);
               }
            }

            String string = key.toString();
            return this.isStrict() ? string.substring(0, Math.min(6, string.length())) : string;
         }
      }
   }
}
