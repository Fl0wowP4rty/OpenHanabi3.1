package cn.hanabi.gui.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;

public class Shader {
   private static final Minecraft mc = Minecraft.getMinecraft();
   private Framebuffer frameBuffer;
   private ShaderLoader clientShader;
   private final String fragmentShader;

   public Shader(String fragmentShader) {
      this.fragmentShader = fragmentShader;
   }

   public void startShader() {
      if (mc.gameSettings.guiScale != 2 && mc.currentScreen == null) {
         mc.gameSettings.guiScale = 2;
      }

      if (this.frameBuffer == null) {
         this.frameBuffer = new Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, false);
      } else if (this.frameBuffer.framebufferWidth != Minecraft.getMinecraft().displayWidth || this.frameBuffer.framebufferHeight != Minecraft.getMinecraft().displayHeight) {
         this.frameBuffer.unbindFramebuffer();
         this.frameBuffer = new Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, false);
         if (this.clientShader != null) {
            this.clientShader.delete();
            new ScaledResolution(mc);
            this.clientShader = new ShaderLoader(this.fragmentShader, this.frameBuffer.framebufferTexture);
         }
      }

      if (this.clientShader == null) {
         new ScaledResolution(mc);
         this.clientShader = new ShaderLoader(this.fragmentShader, this.frameBuffer.framebufferTexture);
      }

      GL11.glMatrixMode(5888);
      RenderHelper.enableStandardItemLighting();
      this.frameBuffer.bindFramebuffer(false);
      GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      GL11.glClear(16640);
   }

   public void stopShader() {
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      this.clientShader.update();
      Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
      ScaledResolution sr = new ScaledResolution(mc);
      GL11.glEnable(3553);
      GL11.glBindTexture(3553, this.clientShader.getFboTextureID());
      GL11.glBegin(4);
      GL11.glTexCoord2d(0.0, 1.0);
      GL11.glVertex2d(0.0, 0.0);
      GL11.glTexCoord2d(0.0, 0.0);
      double x = 0.0;
      double y = 0.0;
      double width = (double)sr.getScaledWidth();
      double height = (double)sr.getScaledHeight();
      GL11.glVertex2d(x, y + height * 2.0);
      GL11.glTexCoord2d(1.0, 0.0);
      GL11.glVertex2d(x + width * 2.0, y + height * 2.0);
      GL11.glTexCoord2d(1.0, 0.0);
      GL11.glVertex2d(x + width * 2.0, y + height * 2.0);
      GL11.glTexCoord2d(1.0, 1.0);
      GL11.glVertex2d(x + width * 2.0, y);
      GL11.glTexCoord2d(0.0, 1.0);
      GL11.glVertex2d(0.0, 0.0);
      GL11.glEnd();
   }

   public void deleteShader() {
      try {
         this.clientShader.delete();
         this.frameBuffer.unbindFramebuffer();
         this.frameBuffer.unbindFramebufferTexture();
         this.clientShader = null;
         this.frameBuffer = null;
      } catch (Exception var2) {
         var2.printStackTrace();
      }

   }
}
