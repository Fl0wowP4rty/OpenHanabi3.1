package com.sun.prism.shader;

import com.sun.prism.ps.Shader;
import com.sun.prism.ps.ShaderFactory;
import java.io.InputStream;
import java.util.HashMap;

public class Solid_TextureFirstPassLCD_Loader {
   private Solid_TextureFirstPassLCD_Loader() {
   }

   public static Shader loadShader(ShaderFactory var0, InputStream var1) {
      HashMap var2 = new HashMap();
      var2.put("inputTex", 0);
      HashMap var3 = new HashMap();
      return var0.createShader(var1, var2, var3, 0, false, true);
   }
}
