package com.sun.scenario.effect.impl.sw.sse;

import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.scenario.effect.Effect;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.ImageData;
import com.sun.scenario.effect.PhongLighting;
import com.sun.scenario.effect.impl.BufferUtil;
import com.sun.scenario.effect.impl.HeapImage;
import com.sun.scenario.effect.impl.Renderer;
import com.sun.scenario.effect.impl.state.RenderState;
import com.sun.scenario.effect.light.PointLight;
import com.sun.scenario.effect.light.SpotLight;
import java.nio.FloatBuffer;

public class SSEPhongLighting_SPOTPeer extends SSEEffectPeer {
   private FloatBuffer kvals;

   public SSEPhongLighting_SPOTPeer(FilterContext var1, Renderer var2, String var3) {
      super(var1, var2, var3);
   }

   protected final PhongLighting getEffect() {
      return (PhongLighting)super.getEffect();
   }

   private float getSurfaceScale() {
      return this.getEffect().getSurfaceScale();
   }

   private float getDiffuseConstant() {
      return this.getEffect().getDiffuseConstant();
   }

   private float getSpecularConstant() {
      return this.getEffect().getSpecularConstant();
   }

   private float getSpecularExponent() {
      return this.getEffect().getSpecularExponent();
   }

   private float[] getNormalizedLightPosition() {
      return this.getEffect().getLight().getNormalizedLightPosition();
   }

   private float[] getLightPosition() {
      PointLight var1 = (PointLight)this.getEffect().getLight();
      return new float[]{var1.getX(), var1.getY(), var1.getZ()};
   }

   private float[] getLightColor() {
      return this.getEffect().getLight().getColor().getPremultipliedRGBComponents();
   }

   private float getLightSpecularExponent() {
      return ((SpotLight)this.getEffect().getLight()).getSpecularExponent();
   }

   private float[] getNormalizedLightDirection() {
      return ((SpotLight)this.getEffect().getLight()).getNormalizedLightDirection();
   }

   private FloatBuffer getKvals() {
      Rectangle var1 = this.getInputNativeBounds(0);
      float var2 = 1.0F / (float)var1.width;
      float var3 = 1.0F / (float)var1.height;
      float[] var4 = new float[]{-1.0F, 0.0F, 1.0F, -2.0F, 0.0F, 2.0F, -1.0F, 0.0F, 1.0F};
      float[] var5 = new float[]{-1.0F, -2.0F, -1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F};
      if (this.kvals == null) {
         this.kvals = BufferUtil.newFloatBuffer(32);
      }

      this.kvals.clear();
      int var6 = 0;
      float var7 = -this.getSurfaceScale() * 0.25F;

      for(int var8 = -1; var8 <= 1; ++var8) {
         for(int var9 = -1; var9 <= 1; ++var9) {
            if (var8 != 0 || var9 != 0) {
               this.kvals.put((float)var9 * var2);
               this.kvals.put((float)var8 * var3);
               this.kvals.put(var4[var6] * var7);
               this.kvals.put(var5[var6] * var7);
            }

            ++var6;
         }
      }

      this.kvals.rewind();
      return this.kvals;
   }

   private int getKvalsArrayLength() {
      return 8;
   }

   public ImageData filter(Effect var1, RenderState var2, BaseTransform var3, Rectangle var4, ImageData... var5) {
      this.setEffect(var1);
      Rectangle var6 = this.getResultBounds(var3, var4, var5);
      this.setDestBounds(var6);
      HeapImage var7 = (HeapImage)var5[0].getTransformedImage(var6);
      byte var8 = 0;
      byte var9 = 0;
      int var10 = var7.getPhysicalWidth();
      int var11 = var7.getPhysicalHeight();
      int var12 = var7.getScanlineStride();
      int[] var13 = var7.getPixelArray();
      Rectangle var14 = new Rectangle(var8, var9, var10, var11);
      Rectangle var15 = var5[0].getTransformedBounds(var6);
      BaseTransform var16 = BaseTransform.IDENTITY_TRANSFORM;
      this.setInputBounds(0, var15);
      this.setInputNativeBounds(0, var14);
      HeapImage var17 = (HeapImage)var5[1].getTransformedImage(var6);
      byte var18 = 0;
      byte var19 = 0;
      int var20 = var17.getPhysicalWidth();
      int var21 = var17.getPhysicalHeight();
      int var22 = var17.getScanlineStride();
      int[] var23 = var17.getPixelArray();
      Rectangle var24 = new Rectangle(var18, var19, var20, var21);
      Rectangle var25 = var5[1].getTransformedBounds(var6);
      BaseTransform var26 = BaseTransform.IDENTITY_TRANSFORM;
      this.setInputBounds(1, var25);
      this.setInputNativeBounds(1, var24);
      float[] var27 = new float[4];
      this.getTextureCoordinates(0, var27, (float)var15.x, (float)var15.y, (float)var10, (float)var11, var6, var16);
      float[] var28 = new float[4];
      this.getTextureCoordinates(1, var28, (float)var25.x, (float)var25.y, (float)var20, (float)var21, var6, var26);
      int var31 = var6.width;
      int var32 = var6.height;
      HeapImage var33 = (HeapImage)this.getRenderer().getCompatibleImage(var31, var32);
      this.setDestNativeBounds(var33.getPhysicalWidth(), var33.getPhysicalHeight());
      int var34 = var33.getScanlineStride();
      int[] var35 = var33.getPixelArray();
      float var36 = this.getDiffuseConstant();
      FloatBuffer var37 = this.getKvals();
      float[] var38 = new float[var37.capacity()];
      var37.get(var38);
      float[] var39 = this.getLightColor();
      float[] var40 = this.getLightPosition();
      float var41 = this.getLightSpecularExponent();
      float[] var42 = this.getNormalizedLightDirection();
      float var43 = this.getSpecularConstant();
      float var44 = this.getSpecularExponent();
      float var45 = this.getSurfaceScale();
      filter(var35, 0, 0, var31, var32, var34, var13, var27[0], var27[1], var27[2], var27[3], var10, var11, var12, var36, var38, var39[0], var39[1], var39[2], var40[0], var40[1], var40[2], var41, var42[0], var42[1], var42[2], var23, var28[0], var28[1], var28[2], var28[3], var20, var21, var22, var43, var44, var45);
      var5[0].releaseTransformedImage(var7);
      var5[1].releaseTransformedImage(var17);
      return new ImageData(this.getFilterContext(), var33, var6);
   }

   private static native void filter(int[] var0, int var1, int var2, int var3, int var4, int var5, int[] var6, float var7, float var8, float var9, float var10, int var11, int var12, int var13, float var14, float[] var15, float var16, float var17, float var18, float var19, float var20, float var21, float var22, float var23, float var24, float var25, int[] var26, float var27, float var28, float var29, float var30, int var31, int var32, int var33, float var34, float var35, float var36);
}
