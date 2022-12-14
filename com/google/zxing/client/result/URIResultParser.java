package com.google.zxing.client.result;

import com.google.zxing.Result;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class URIResultParser extends ResultParser {
   private static final String ALPHANUM_PART = "[a-zA-Z0-9\\-]";
   private static final Pattern URL_WITH_PROTOCOL_PATTERN = Pattern.compile("[a-zA-Z0-9]{2,}:");
   private static final Pattern URL_WITHOUT_PROTOCOL_PATTERN = Pattern.compile("([a-zA-Z0-9\\-]+\\.)+[a-zA-Z0-9\\-]{2,}(:\\d{1,5})?(/|\\?|$)");

   public URIParsedResult parse(Result result) {
      String rawText = getMassagedText(result);
      if (!rawText.startsWith("URL:") && !rawText.startsWith("URI:")) {
         rawText = rawText.trim();
         return isBasicallyValidURI(rawText) ? new URIParsedResult(rawText, (String)null) : null;
      } else {
         return new URIParsedResult(rawText.substring(4).trim(), (String)null);
      }
   }

   static boolean isBasicallyValidURI(CharSequence uri) {
      Matcher m = URL_WITH_PROTOCOL_PATTERN.matcher(uri);
      if (m.find() && m.start() == 0) {
         return true;
      } else {
         m = URL_WITHOUT_PROTOCOL_PATTERN.matcher(uri);
         return m.find() && m.start() == 0;
      }
   }
}
