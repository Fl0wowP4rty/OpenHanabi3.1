package me.theresa.fontRenderer.font;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.security.AccessController;
import java.util.ArrayList;
import me.theresa.fontRenderer.font.geom.Rectangle;
import me.theresa.fontRenderer.font.geom.Shape;
import me.theresa.fontRenderer.font.geom.ShapeRenderer;
import me.theresa.fontRenderer.font.impl.Font;
import me.theresa.fontRenderer.font.impl.ShapeFill;
import me.theresa.fontRenderer.font.log.FastTrig;
import me.theresa.fontRenderer.font.log.Log;
import me.theresa.fontRenderer.font.opengl.TextureImpl;
import me.theresa.fontRenderer.font.opengl.renderer.LineStripRenderer;
import me.theresa.fontRenderer.font.opengl.renderer.Renderer;
import me.theresa.fontRenderer.font.opengl.renderer.SGL;
import org.lwjgl.BufferUtils;

public class Graphics {
   protected static SGL GL = Renderer.get();
   private static final LineStripRenderer LSR = Renderer.getLineStripRenderer();
   public static int MODE_NORMAL = 1;
   public static int MODE_ALPHA_MAP = 2;
   public static int MODE_ALPHA_BLEND = 3;
   public static int MODE_COLOR_MULTIPLY = 4;
   public static int MODE_ADD = 5;
   public static int MODE_SCREEN = 6;
   private static final int DEFAULT_SEGMENTS = 50;
   protected static Graphics currentGraphics = null;
   protected static Font DEFAULT_FONT;
   private float sx = 1.0F;
   private float sy = 1.0F;
   private Font font;
   private Color currentColor;
   protected int screenWidth;
   protected int screenHeight;
   private boolean pushed;
   private Rectangle clip;
   private final DoubleBuffer worldClip;
   private final ByteBuffer readBuffer;
   private boolean antialias;
   private Rectangle worldClipRecord;
   private int currentDrawingMode;
   private float lineWidth;
   private final ArrayList stack;
   private int stackIndex;

   public static void setCurrent(Graphics current) {
      if (currentGraphics != current) {
         if (currentGraphics != null) {
            currentGraphics.disable();
         }

         currentGraphics = current;
         currentGraphics.enable();
      }

   }

   public Graphics() {
      this.currentColor = Color.white;
      this.worldClip = BufferUtils.createDoubleBuffer(4);
      this.readBuffer = BufferUtils.createByteBuffer(4);
      this.currentDrawingMode = MODE_NORMAL;
      this.lineWidth = 1.0F;
      this.stack = new ArrayList();
   }

   public Graphics(int width, int height) {
      this.currentColor = Color.white;
      this.worldClip = BufferUtils.createDoubleBuffer(4);
      this.readBuffer = BufferUtils.createByteBuffer(4);
      this.currentDrawingMode = MODE_NORMAL;
      this.lineWidth = 1.0F;
      this.stack = new ArrayList();
      if (DEFAULT_FONT == null) {
         AccessController.doPrivileged(() -> {
            try {
               DEFAULT_FONT = new AngelCodeFont("org/newdawn/slick/data/defaultfont.fnt", "org/newdawn/slick/data/defaultfont.png");
            } catch (SlickException var1) {
               Log.error((Throwable)var1);
            }

            return null;
         });
      }

      this.font = DEFAULT_FONT;
      this.screenWidth = width;
      this.screenHeight = height;
   }

   void setDimensions(int width, int height) {
      this.screenWidth = width;
      this.screenHeight = height;
   }

   public void setDrawMode(int mode) {
      this.predraw();
      this.currentDrawingMode = mode;
      if (this.currentDrawingMode == MODE_NORMAL) {
         GL.glEnable(3042);
         GL.glColorMask(true, true, true, true);
         GL.glBlendFunc(770, 771);
      }

      if (this.currentDrawingMode == MODE_ALPHA_MAP) {
         GL.glDisable(3042);
         GL.glColorMask(false, false, false, true);
      }

      if (this.currentDrawingMode == MODE_ALPHA_BLEND) {
         GL.glEnable(3042);
         GL.glColorMask(true, true, true, false);
         GL.glBlendFunc(772, 773);
      }

      if (this.currentDrawingMode == MODE_COLOR_MULTIPLY) {
         GL.glEnable(3042);
         GL.glColorMask(true, true, true, true);
         GL.glBlendFunc(769, 768);
      }

      if (this.currentDrawingMode == MODE_ADD) {
         GL.glEnable(3042);
         GL.glColorMask(true, true, true, true);
         GL.glBlendFunc(1, 1);
      }

      if (this.currentDrawingMode == MODE_SCREEN) {
         GL.glEnable(3042);
         GL.glColorMask(true, true, true, true);
         GL.glBlendFunc(1, 769);
      }

      this.postdraw();
   }

   public void clearAlphaMap() {
      this.pushTransform();
      GL.glLoadIdentity();
      int originalMode = this.currentDrawingMode;
      this.setDrawMode(MODE_ALPHA_MAP);
      this.setColor(new Color(0, 0, 0, 0));
      this.fillRect(0.0F, 0.0F, (float)this.screenWidth, (float)this.screenHeight);
      this.setColor(this.currentColor);
      this.setDrawMode(originalMode);
      this.popTransform();
   }

   private void predraw() {
      setCurrent(this);
   }

   private void postdraw() {
   }

   protected void enable() {
   }

   public void flush() {
      if (currentGraphics == this) {
         currentGraphics.disable();
         currentGraphics = null;
      }

   }

   protected void disable() {
   }

   public Font getFont() {
      return this.font;
   }

   public void setBackground(Color color) {
      this.predraw();
      GL.glClearColor(color.r, color.g, color.b, color.a);
      this.postdraw();
   }

   public Color getBackground() {
      this.predraw();
      FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
      GL.glGetFloat(3106, buffer);
      this.postdraw();
      return new Color(buffer);
   }

   public void clear() {
      this.predraw();
      GL.glClear(16384);
      this.postdraw();
   }

   public void resetTransform() {
      this.sx = 1.0F;
      this.sy = 1.0F;
      if (this.pushed) {
         this.predraw();
         GL.glPopMatrix();
         this.pushed = false;
         this.postdraw();
      }

   }

   private void checkPush() {
      if (!this.pushed) {
         this.predraw();
         GL.glPushMatrix();
         this.pushed = true;
         this.postdraw();
      }

   }

   public void scale(float sx, float sy) {
      this.sx *= sx;
      this.sy *= sy;
      this.checkPush();
      this.predraw();
      GL.glScalef(sx, sy, 1.0F);
      this.postdraw();
   }

   public void rotate(float rx, float ry, float ang) {
      this.checkPush();
      this.predraw();
      this.translate(rx, ry);
      GL.glRotatef(ang, 0.0F, 0.0F, 1.0F);
      this.translate(-rx, -ry);
      this.postdraw();
   }

   public void translate(float x, float y) {
      this.checkPush();
      this.predraw();
      GL.glTranslatef(x, y, 0.0F);
      this.postdraw();
   }

   public void setFont(Font font) {
      this.font = font;
   }

   public void resetFont() {
      this.font = DEFAULT_FONT;
   }

   public void setColor(Color color) {
      if (color != null) {
         this.currentColor = new Color(color);
         this.predraw();
         this.currentColor.bind();
         this.postdraw();
      }
   }

   public Color getColor() {
      return new Color(this.currentColor);
   }

   public void drawLine(float x1, float y1, float x2, float y2) {
      float lineWidth = this.lineWidth - 1.0F;
      if (LSR.applyGLLineFixes()) {
         float step;
         if (x1 == x2) {
            if (y1 > y2) {
               step = y2;
               y2 = y1;
               y1 = step;
            }

            step = 1.0F / this.sy;
            lineWidth /= this.sy;
            this.fillRect(x1 - lineWidth / 2.0F, y1 - lineWidth / 2.0F, lineWidth + step, y2 - y1 + lineWidth + step);
            return;
         }

         if (y1 == y2) {
            if (x1 > x2) {
               step = x2;
               x2 = x1;
               x1 = step;
            }

            step = 1.0F / this.sx;
            lineWidth /= this.sx;
            this.fillRect(x1 - lineWidth / 2.0F, y1 - lineWidth / 2.0F, x2 - x1 + lineWidth + step, lineWidth + step);
            return;
         }
      }

      this.predraw();
      this.currentColor.bind();
      TextureImpl.bindNone();
      LSR.start();
      LSR.vertex(x1, y1);
      LSR.vertex(x2, y2);
      LSR.end();
      this.postdraw();
   }

   public void draw(Shape shape, ShapeFill fill) {
      this.predraw();
      TextureImpl.bindNone();
      ShapeRenderer.draw(shape, fill);
      this.currentColor.bind();
      this.postdraw();
   }

   public void fill(Shape shape, ShapeFill fill) {
      this.predraw();
      TextureImpl.bindNone();
      ShapeRenderer.fill(shape, fill);
      this.currentColor.bind();
      this.postdraw();
   }

   public void draw(Shape shape) {
      this.predraw();
      TextureImpl.bindNone();
      this.currentColor.bind();
      ShapeRenderer.draw(shape);
      this.postdraw();
   }

   public void fill(Shape shape) {
      this.predraw();
      TextureImpl.bindNone();
      this.currentColor.bind();
      ShapeRenderer.fill(shape);
      this.postdraw();
   }

   public void texture(Shape shape, Image image) {
      this.texture(shape, image, 0.01F, 0.01F, false);
   }

   public void texture(Shape shape, Image image, ShapeFill fill) {
      this.texture(shape, image, 0.01F, 0.01F, fill);
   }

   public void texture(Shape shape, Image image, boolean fit) {
      if (fit) {
         this.texture(shape, image, 1.0F, 1.0F, true);
      } else {
         this.texture(shape, image, 0.01F, 0.01F, false);
      }

   }

   public void texture(Shape shape, Image image, float scaleX, float scaleY) {
      this.texture(shape, image, scaleX, scaleY, false);
   }

   public void texture(Shape shape, Image image, float scaleX, float scaleY, boolean fit) {
      this.predraw();
      TextureImpl.bindNone();
      this.currentColor.bind();
      if (fit) {
         ShapeRenderer.textureFit(shape, image, scaleX, scaleY);
      } else {
         ShapeRenderer.texture(shape, image, scaleX, scaleY);
      }

      this.postdraw();
   }

   public void texture(Shape shape, Image image, float scaleX, float scaleY, ShapeFill fill) {
      this.predraw();
      TextureImpl.bindNone();
      this.currentColor.bind();
      ShapeRenderer.texture(shape, image, scaleX, scaleY, fill);
      this.postdraw();
   }

   public void drawRect(float x1, float y1, float width, float height) {
      float lineWidth = this.getLineWidth();
      this.drawLine(x1, y1, x1 + width, y1);
      this.drawLine(x1 + width, y1, x1 + width, y1 + height);
      this.drawLine(x1 + width, y1 + height, x1, y1 + height);
      this.drawLine(x1, y1 + height, x1, y1);
   }

   public void clearClip() {
      this.clip = null;
      this.predraw();
      GL.glDisable(3089);
      this.postdraw();
   }

   public void setWorldClip(float x, float y, float width, float height) {
      this.predraw();
      this.worldClipRecord = new Rectangle(x, y, width, height);
      GL.glEnable(12288);
      this.worldClip.put(1.0).put(0.0).put(0.0).put((double)(-x)).flip();
      GL.glClipPlane(12288, this.worldClip);
      GL.glEnable(12289);
      this.worldClip.put(-1.0).put(0.0).put(0.0).put((double)(x + width)).flip();
      GL.glClipPlane(12289, this.worldClip);
      GL.glEnable(12290);
      this.worldClip.put(0.0).put(1.0).put(0.0).put((double)(-y)).flip();
      GL.glClipPlane(12290, this.worldClip);
      GL.glEnable(12291);
      this.worldClip.put(0.0).put(-1.0).put(0.0).put((double)(y + height)).flip();
      GL.glClipPlane(12291, this.worldClip);
      this.postdraw();
   }

   public void clearWorldClip() {
      this.predraw();
      this.worldClipRecord = null;
      GL.glDisable(12288);
      GL.glDisable(12289);
      GL.glDisable(12290);
      GL.glDisable(12291);
      this.postdraw();
   }

   public void setWorldClip(Rectangle clip) {
      if (clip == null) {
         this.clearWorldClip();
      } else {
         this.setWorldClip(clip.getX(), clip.getY(), clip.getWidth(), clip.getHeight());
      }

   }

   public Rectangle getWorldClip() {
      return this.worldClipRecord;
   }

   public void setClip(int x, int y, int width, int height) {
      this.predraw();
      if (this.clip == null) {
         GL.glEnable(3089);
         this.clip = new Rectangle((float)x, (float)y, (float)width, (float)height);
      } else {
         this.clip.setBounds((float)x, (float)y, (float)width, (float)height);
      }

      GL.glScissor(x, this.screenHeight - y - height, width, height);
      this.postdraw();
   }

   public void setClip(Rectangle rect) {
      if (rect == null) {
         this.clearClip();
      } else {
         this.setClip((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
      }
   }

   public Rectangle getClip() {
      return this.clip;
   }

   public void fillRect(float x, float y, float width, float height, Image pattern, float offX, float offY) {
      int cols = (int)Math.ceil((double)(width / (float)pattern.getWidth())) + 2;
      int rows = (int)Math.ceil((double)(height / (float)pattern.getHeight())) + 2;
      Rectangle preClip = this.getWorldClip();
      this.setWorldClip(x, y, width, height);
      this.predraw();

      for(int c = 0; c < cols; ++c) {
         for(int r = 0; r < rows; ++r) {
            pattern.draw((float)(c * pattern.getWidth()) + x - offX, (float)(r * pattern.getHeight()) + y - offY);
         }
      }

      this.postdraw();
      this.setWorldClip(preClip);
   }

   public void fillRect(float x1, float y1, float width, float height) {
      this.predraw();
      TextureImpl.bindNone();
      this.currentColor.bind();
      GL.glBegin(7);
      GL.glVertex2f(x1, y1);
      GL.glVertex2f(x1 + width, y1);
      GL.glVertex2f(x1 + width, y1 + height);
      GL.glVertex2f(x1, y1 + height);
      GL.glEnd();
      this.postdraw();
   }

   public void drawOval(float x1, float y1, float width, float height) {
      this.drawOval(x1, y1, width, height, 50);
   }

   public void drawOval(float x1, float y1, float width, float height, int segments) {
      this.drawArc(x1, y1, width, height, segments, 0.0F, 360.0F);
   }

   public void drawArc(float x1, float y1, float width, float height, float start, float end) {
      this.drawArc(x1, y1, width, height, 50, start, end);
   }

   public void drawArc(float x1, float y1, float width, float height, int segments, float start, float end) {
      this.predraw();
      TextureImpl.bindNone();
      this.currentColor.bind();

      while(end < start) {
         end += 360.0F;
      }

      float cx = x1 + width / 2.0F;
      float cy = y1 + height / 2.0F;
      LSR.start();
      int step = 360 / segments;

      for(int a = (int)start; a < (int)(end + (float)step); a += step) {
         float ang = (float)a;
         if (ang > end) {
            ang = end;
         }

         float x = (float)((double)cx + FastTrig.cos(Math.toRadians((double)ang)) * (double)width / 2.0);
         float y = (float)((double)cy + FastTrig.sin(Math.toRadians((double)ang)) * (double)height / 2.0);
         LSR.vertex(x, y);
      }

      LSR.end();
      this.postdraw();
   }

   public void fillOval(float x1, float y1, float width, float height) {
      this.fillOval(x1, y1, width, height, 50);
   }

   public void fillOval(float x1, float y1, float width, float height, int segments) {
      this.fillArc(x1, y1, width, height, segments, 0.0F, 360.0F);
   }

   public void fillArc(float x1, float y1, float width, float height, float start, float end) {
      this.fillArc(x1, y1, width, height, 50, start, end);
   }

   public void fillArc(float x1, float y1, float width, float height, int segments, float start, float end) {
      this.predraw();
      TextureImpl.bindNone();
      this.currentColor.bind();

      while(end < start) {
         end += 360.0F;
      }

      float cx = x1 + width / 2.0F;
      float cy = y1 + height / 2.0F;
      GL.glBegin(6);
      int step = 360 / segments;
      GL.glVertex2f(cx, cy);

      int a;
      float ang;
      float x;
      float y;
      for(a = (int)start; a < (int)(end + (float)step); a += step) {
         ang = (float)a;
         if (ang > end) {
            ang = end;
         }

         x = (float)((double)cx + FastTrig.cos(Math.toRadians((double)ang)) * (double)width / 2.0);
         y = (float)((double)cy + FastTrig.sin(Math.toRadians((double)ang)) * (double)height / 2.0);
         GL.glVertex2f(x, y);
      }

      GL.glEnd();
      if (this.antialias) {
         GL.glBegin(6);
         GL.glVertex2f(cx, cy);
         if (end != 360.0F) {
            end -= 10.0F;
         }

         for(a = (int)start; a < (int)(end + (float)step); a += step) {
            ang = (float)a;
            if (ang > end) {
               ang = end;
            }

            x = (float)((double)cx + FastTrig.cos(Math.toRadians((double)(ang + 10.0F))) * (double)width / 2.0);
            y = (float)((double)cy + FastTrig.sin(Math.toRadians((double)(ang + 10.0F))) * (double)height / 2.0);
            GL.glVertex2f(x, y);
         }

         GL.glEnd();
      }

      this.postdraw();
   }

   public void drawRoundRect(float x, float y, float width, float height, int cornerRadius) {
      this.drawRoundRect(x, y, width, height, cornerRadius, 50);
   }

   public void drawRoundRect(float x, float y, float width, float height, int cornerRadius, int segs) {
      if (cornerRadius < 0) {
         throw new IllegalArgumentException("corner radius must be > 0");
      } else if (cornerRadius == 0) {
         this.drawRect(x, y, width, height);
      } else {
         int mr = (int)Math.min(width, height) / 2;
         if (cornerRadius > mr) {
            cornerRadius = mr;
         }

         this.drawLine(x + (float)cornerRadius, y, x + width - (float)cornerRadius, y);
         this.drawLine(x, y + (float)cornerRadius, x, y + height - (float)cornerRadius);
         this.drawLine(x + width, y + (float)cornerRadius, x + width, y + height - (float)cornerRadius);
         this.drawLine(x + (float)cornerRadius, y + height, x + width - (float)cornerRadius, y + height);
         float d = (float)(cornerRadius * 2);
         this.drawArc(x + width - d, y + height - d, d, d, segs, 0.0F, 90.0F);
         this.drawArc(x, y + height - d, d, d, segs, 90.0F, 180.0F);
         this.drawArc(x + width - d, y, d, d, segs, 270.0F, 360.0F);
         this.drawArc(x, y, d, d, segs, 180.0F, 270.0F);
      }
   }

   public void fillRoundRect(float x, float y, float width, float height, int cornerRadius) {
      this.fillRoundRect(x, y, width, height, cornerRadius, 50);
   }

   public void fillRoundRect(float x, float y, float width, float height, int cornerRadius, int segs) {
      if (cornerRadius < 0) {
         throw new IllegalArgumentException("corner radius must be > 0");
      } else if (cornerRadius == 0) {
         this.fillRect(x, y, width, height);
      } else {
         int mr = (int)Math.min(width, height) / 2;
         if (cornerRadius > mr) {
            cornerRadius = mr;
         }

         float d = (float)(cornerRadius * 2);
         this.fillRect(x + (float)cornerRadius, y, width - d, (float)cornerRadius);
         this.fillRect(x, y + (float)cornerRadius, (float)cornerRadius, height - d);
         this.fillRect(x + width - (float)cornerRadius, y + (float)cornerRadius, (float)cornerRadius, height - d);
         this.fillRect(x + (float)cornerRadius, y + height - (float)cornerRadius, width - d, (float)cornerRadius);
         this.fillRect(x + (float)cornerRadius, y + (float)cornerRadius, width - d, height - d);
         this.fillArc(x + width - d, y + height - d, d, d, segs, 0.0F, 90.0F);
         this.fillArc(x, y + height - d, d, d, segs, 90.0F, 180.0F);
         this.fillArc(x + width - d, y, d, d, segs, 270.0F, 360.0F);
         this.fillArc(x, y, d, d, segs, 180.0F, 270.0F);
      }
   }

   public void setLineWidth(float width) {
      this.predraw();
      this.lineWidth = width;
      LSR.setWidth(width);
      GL.glPointSize(width);
      this.postdraw();
   }

   public float getLineWidth() {
      return this.lineWidth;
   }

   public void resetLineWidth() {
      this.predraw();
      Renderer.getLineStripRenderer().setWidth(1.0F);
      GL.glLineWidth(1.0F);
      GL.glPointSize(1.0F);
      this.postdraw();
   }

   public void setAntiAlias(boolean anti) {
      this.predraw();
      this.antialias = anti;
      LSR.setAntiAlias(anti);
      if (anti) {
         GL.glEnable(2881);
      } else {
         GL.glDisable(2881);
      }

      this.postdraw();
   }

   public boolean isAntiAlias() {
      return this.antialias;
   }

   public void drawString(String str, float x, float y) {
      this.predraw();
      this.font.drawString(x, y, str, this.currentColor);
      this.postdraw();
   }

   public void drawImage(Image image, float x, float y, Color col) {
      this.predraw();
      image.draw(x, y, col);
      this.currentColor.bind();
      this.postdraw();
   }

   public void drawAnimation(Animation anim, float x, float y) {
      this.drawAnimation(anim, x, y, Color.white);
   }

   public void drawAnimation(Animation anim, float x, float y, Color col) {
      this.predraw();
      anim.draw(x, y, col);
      this.currentColor.bind();
      this.postdraw();
   }

   public void drawImage(Image image, float x, float y) {
      this.drawImage(image, x, y, Color.white);
   }

   public void drawImage(Image image, float x, float y, float x2, float y2, float srcx, float srcy, float srcx2, float srcy2) {
      this.predraw();
      image.draw(x, y, x2, y2, srcx, srcy, srcx2, srcy2);
      this.currentColor.bind();
      this.postdraw();
   }

   public void drawImage(Image image, float x, float y, float srcx, float srcy, float srcx2, float srcy2) {
      this.drawImage(image, x, y, x + (float)image.getWidth(), y + (float)image.getHeight(), srcx, srcy, srcx2, srcy2);
   }

   public void copyArea(Image target, int x, int y) {
      int format = target.getTexture().hasAlpha() ? 6408 : 6407;
      target.bind();
      GL.glCopyTexImage2D(3553, 0, format, x, this.screenHeight - (y + target.getHeight()), target.getTexture().getTextureWidth(), target.getTexture().getTextureHeight(), 0);
      target.ensureInverted();
   }

   private int translate(byte b) {
      return b < 0 ? 256 + b : b;
   }

   public Color getPixel(int x, int y) {
      this.predraw();
      GL.glReadPixels(x, this.screenHeight - y, 1, 1, 6408, 5121, this.readBuffer);
      this.postdraw();
      return new Color(this.translate(this.readBuffer.get(0)), this.translate(this.readBuffer.get(1)), this.translate(this.readBuffer.get(2)), this.translate(this.readBuffer.get(3)));
   }

   public void getArea(int x, int y, int width, int height, ByteBuffer target) {
      if (target.capacity() < width * height * 4) {
         throw new IllegalArgumentException("Byte buffer provided to get area is not big enough");
      } else {
         this.predraw();
         GL.glReadPixels(x, this.screenHeight - y - height, width, height, 6408, 5121, target);
         this.postdraw();
      }
   }

   public void drawImage(Image image, float x, float y, float x2, float y2, float srcx, float srcy, float srcx2, float srcy2, Color col) {
      this.predraw();
      image.draw(x, y, x2, y2, srcx, srcy, srcx2, srcy2, col);
      this.currentColor.bind();
      this.postdraw();
   }

   public void drawImage(Image image, float x, float y, float srcx, float srcy, float srcx2, float srcy2, Color col) {
      this.drawImage(image, x, y, x + (float)image.getWidth(), y + (float)image.getHeight(), srcx, srcy, srcx2, srcy2, col);
   }

   public void drawGradientLine(float x1, float y1, float red1, float green1, float blue1, float alpha1, float x2, float y2, float red2, float green2, float blue2, float alpha2) {
      this.predraw();
      TextureImpl.bindNone();
      GL.glBegin(1);
      GL.glColor4f(red1, green1, blue1, alpha1);
      GL.glVertex2f(x1, y1);
      GL.glColor4f(red2, green2, blue2, alpha2);
      GL.glVertex2f(x2, y2);
      GL.glEnd();
      this.postdraw();
   }

   public void drawGradientLine(float x1, float y1, Color Color1, float x2, float y2, Color Color2) {
      this.predraw();
      TextureImpl.bindNone();
      GL.glBegin(1);
      Color1.bind();
      GL.glVertex2f(x1, y1);
      Color2.bind();
      GL.glVertex2f(x2, y2);
      GL.glEnd();
      this.postdraw();
   }

   public void pushTransform() {
      this.predraw();
      FloatBuffer buffer;
      if (this.stackIndex >= this.stack.size()) {
         buffer = BufferUtils.createFloatBuffer(18);
         this.stack.add(buffer);
      } else {
         buffer = (FloatBuffer)this.stack.get(this.stackIndex);
      }

      GL.glGetFloat(2982, buffer);
      buffer.put(16, this.sx);
      buffer.put(17, this.sy);
      ++this.stackIndex;
      this.postdraw();
   }

   public void popTransform() {
      if (this.stackIndex == 0) {
         throw new RuntimeException("Attempt to pop a transform that hasn't be pushed");
      } else {
         this.predraw();
         --this.stackIndex;
         FloatBuffer oldBuffer = (FloatBuffer)this.stack.get(this.stackIndex);
         GL.glLoadMatrix(oldBuffer);
         this.sx = oldBuffer.get(16);
         this.sy = oldBuffer.get(17);
         this.postdraw();
      }
   }

   public void destroy() {
   }
}
