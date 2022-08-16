package org.apache.commons.codec.language;

import java.util.Locale;

public class Caverphone1 extends AbstractCaverphone {
   private static final String SIX_1 = "111111";

   public String encode(String source) {
      if (source != null && source.length() != 0) {
         String txt = source.toLowerCase(Locale.ENGLISH);
         txt = txt.replaceAll("[^a-z]", "");
         txt = txt.replaceAll("^cough", "cou2f");
         txt = txt.replaceAll("^rough", "rou2f");
         txt = txt.replaceAll("^tough", "tou2f");
         txt = txt.replaceAll("^enough", "enou2f");
         txt = txt.replaceAll("^gn", "2n");
         txt = txt.replaceAll("mb$", "m2");
         txt = txt.replaceAll("cq", "2q");
         txt = txt.replaceAll("ci", "si");
         txt = txt.replaceAll("ce", "se");
         txt = txt.replaceAll("cy", "sy");
         txt = txt.replaceAll("tch", "2ch");
         txt = txt.replaceAll("c", "k");
         txt = txt.replaceAll("q", "k");
         txt = txt.replaceAll("x", "k");
         txt = txt.replaceAll("v", "f");
         txt = txt.replaceAll("dg", "2g");
         txt = txt.replaceAll("tio", "sio");
         txt = txt.replaceAll("tia", "sia");
         txt = txt.replaceAll("d", "t");
         txt = txt.replaceAll("ph", "fh");
         txt = txt.replaceAll("b", "p");
         txt = txt.replaceAll("sh", "s2");
         txt = txt.replaceAll("z", "s");
         txt = txt.replaceAll("^[aeiou]", "A");
         txt = txt.replaceAll("[aeiou]", "3");
         txt = txt.replaceAll("3gh3", "3kh3");
         txt = txt.replaceAll("gh", "22");
         txt = txt.replaceAll("g", "k");
         txt = txt.replaceAll("s+", "S");
         txt = txt.replaceAll("t+", "T");
         txt = txt.replaceAll("p+", "P");
         txt = txt.replaceAll("k+", "K");
         txt = txt.replaceAll("f+", "F");
         txt = txt.replaceAll("m+", "M");
         txt = txt.replaceAll("n+", "N");
         txt = txt.replaceAll("w3", "W3");
         txt = txt.replaceAll("wy", "Wy");
         txt = txt.replaceAll("wh3", "Wh3");
         txt = txt.replaceAll("why", "Why");
         txt = txt.replaceAll("w", "2");
         txt = txt.replaceAll("^h", "A");
         txt = txt.replaceAll("h", "2");
         txt = txt.replaceAll("r3", "R3");
         txt = txt.replaceAll("ry", "Ry");
         txt = txt.replaceAll("r", "2");
         txt = txt.replaceAll("l3", "L3");
         txt = txt.replaceAll("ly", "Ly");
         txt = txt.replaceAll("l", "2");
         txt = txt.replaceAll("j", "y");
         txt = txt.replaceAll("y3", "Y3");
         txt = txt.replaceAll("y", "2");
         txt = txt.replaceAll("2", "");
         txt = txt.replaceAll("3", "");
         txt = txt + "111111";
         return txt.substring(0, "111111".length());
      } else {
         return "111111";
      }
   }
}
