package com.sun.prism.ps;

import com.sun.javafx.geom.transform.Affine3D;
import com.sun.prism.Graphics;
import com.sun.prism.Texture;

public interface ShaderGraphics extends Graphics {
   void getPaintShaderTransform(Affine3D var1);

   void setExternalShader(Shader var1);

   void drawTextureRaw2(Texture var1, Texture var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14);

   void drawMappedTextureRaw2(Texture var1, Texture var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, float var15, float var16, float var17, float var18, float var19, float var20, float var21, float var22);
}
