package com.sun.prism.shader;

import com.sun.prism.ps.Shader;
import com.sun.prism.ps.ShaderFactory;
import java.io.InputStream;
import java.util.HashMap;

public class DrawSemiRoundRect_ImagePattern_Loader {
   private DrawSemiRoundRect_ImagePattern_Loader() {
   }

   public static Shader loadShader(ShaderFactory var0, InputStream var1) {
      HashMap var2 = new HashMap();
      var2.put("inputTex", 0);
      HashMap var3 = new HashMap();
      var3.put("perspVec", 4);
      var3.put("xParams", 2);
      var3.put("idim", 1);
      var3.put("yParams", 3);
      var3.put("content", 5);
      var3.put("oinvarcradii", 0);
      return var0.createShader(var1, var2, var3, 1, true, true);
   }
}
