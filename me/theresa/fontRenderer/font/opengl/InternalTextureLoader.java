package me.theresa.fontRenderer.font.opengl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Iterator;
import me.theresa.fontRenderer.font.opengl.renderer.Renderer;
import me.theresa.fontRenderer.font.opengl.renderer.SGL;
import me.theresa.fontRenderer.font.util.ResourceLoader;
import org.lwjgl.BufferUtils;

public class InternalTextureLoader {
   protected static SGL GL = Renderer.get();
   private static final InternalTextureLoader loader = new InternalTextureLoader();
   private final HashMap texturesLinear = new HashMap();
   private final HashMap texturesNearest = new HashMap();
   private int dstPixelFormat = 6408;
   private boolean deferred;
   private boolean holdTextureData;

   public static InternalTextureLoader get() {
      return loader;
   }

   private InternalTextureLoader() {
   }

   public void setHoldTextureData(boolean holdTextureData) {
      this.holdTextureData = holdTextureData;
   }

   public void setDeferredLoading(boolean deferred) {
      this.deferred = deferred;
   }

   public boolean isDeferredLoading() {
      return this.deferred;
   }

   public void clear(String name) {
      this.texturesLinear.remove(name);
      this.texturesNearest.remove(name);
   }

   public void clear() {
      this.texturesLinear.clear();
      this.texturesNearest.clear();
   }

   public void set16BitMode() {
      this.dstPixelFormat = 32859;
   }

   public static int createTextureID() {
      IntBuffer tmp = createIntBuffer(1);
      GL.glGenTextures(tmp);
      return tmp.get(0);
   }

   public Texture getTexture(File source, boolean flipped, int filter) throws IOException {
      String resourceName = source.getAbsolutePath();
      InputStream in = new FileInputStream(source);
      return this.getTexture(in, resourceName, flipped, filter, (int[])null);
   }

   public Texture getTexture(File source, boolean flipped, int filter, int[] transparent) throws IOException {
      String resourceName = source.getAbsolutePath();
      InputStream in = new FileInputStream(source);
      return this.getTexture(in, resourceName, flipped, filter, transparent);
   }

   public Texture getTexture(String resourceName, boolean flipped, int filter) throws IOException {
      InputStream in = ResourceLoader.getResourceAsStream(resourceName);
      return this.getTexture(in, resourceName, flipped, filter, (int[])null);
   }

   public Texture getTexture(String resourceName, boolean flipped, int filter, int[] transparent) throws IOException {
      InputStream in = ResourceLoader.getResourceAsStream(resourceName);
      return this.getTexture(in, resourceName, flipped, filter, transparent);
   }

   public Texture getTexture(InputStream in, String resourceName, boolean flipped, int filter) throws IOException {
      return this.getTexture(in, resourceName, flipped, filter, (int[])null);
   }

   public TextureImpl getTexture(InputStream in, String resourceName, boolean flipped, int filter, int[] transparent) throws IOException {
      if (this.deferred) {
         return new DeferredTexture(in, resourceName, flipped, filter, transparent);
      } else {
         HashMap hash = this.texturesLinear;
         if (filter == 9728) {
            hash = this.texturesNearest;
         }

         String resName = resourceName;
         if (transparent != null) {
            resName = resourceName + ":" + transparent[0] + ":" + transparent[1] + ":" + transparent[2];
         }

         resName = resName + ":" + flipped;
         TextureImpl tex;
         if (this.holdTextureData) {
            tex = (TextureImpl)hash.get(resName);
            if (tex != null) {
               return tex;
            }
         } else {
            SoftReference ref = (SoftReference)hash.get(resName);
            if (ref != null) {
               TextureImpl tex = (TextureImpl)ref.get();
               if (tex != null) {
                  return tex;
               }

               hash.remove(resName);
            }
         }

         try {
            GL.glGetError();
         } catch (NullPointerException var10) {
            throw new RuntimeException("Image based resources must be loaded as part of init() or the game loop. They cannot be loaded before initialisation.");
         }

         tex = this.getTexture(in, resourceName, 3553, filter, filter, flipped, transparent);
         tex.setCacheName(resName);
         if (this.holdTextureData) {
            hash.put(resName, tex);
         } else {
            hash.put(resName, new SoftReference(tex));
         }

         return tex;
      }
   }

   private TextureImpl getTexture(InputStream in, String resourceName, int target, int magFilter, int minFilter, boolean flipped, int[] transparent) throws IOException {
      LoadableImageData imageData = ImageDataFactory.getImageDataFor(resourceName);
      ByteBuffer textureBuffer = imageData.loadImage(new BufferedInputStream(in), flipped, transparent);
      int textureID = createTextureID();
      TextureImpl texture = new TextureImpl(resourceName, target, textureID);
      GL.glBindTexture(target, textureID);
      int width = imageData.getWidth();
      int height = imageData.getHeight();
      boolean hasAlpha = imageData.getDepth() == 32;
      texture.setTextureWidth(imageData.getTexWidth());
      texture.setTextureHeight(imageData.getTexHeight());
      int texWidth = texture.getTextureWidth();
      int texHeight = texture.getTextureHeight();
      IntBuffer temp = BufferUtils.createIntBuffer(16);
      GL.glGetInteger(3379, temp);
      int max = temp.get(0);
      if (texWidth <= max && texHeight <= max) {
         int srcPixelFormat = hasAlpha ? 6408 : 6407;
         int componentCount = hasAlpha ? 4 : 3;
         texture.setWidth(width);
         texture.setHeight(height);
         texture.setAlpha(hasAlpha);
         if (this.holdTextureData) {
            texture.setTextureData(srcPixelFormat, componentCount, minFilter, magFilter, textureBuffer);
         }

         SGL var10002 = GL;
         GL.glTexParameteri(target, 10241, minFilter);
         var10002 = GL;
         GL.glTexParameteri(target, 10240, magFilter);
         GL.glTexImage2D(target, 0, this.dstPixelFormat, get2Fold(width), get2Fold(height), 0, srcPixelFormat, 5121, textureBuffer);
         return texture;
      } else {
         throw new IOException("Attempt to allocate a texture to big for the current hardware");
      }
   }

   public Texture createTexture(int width, int height) throws IOException {
      return this.createTexture(width, height, 9728);
   }

   public Texture createTexture(int width, int height, int filter) throws IOException {
      ImageData ds = new EmptyImageData(width, height);
      return this.getTexture(ds, filter);
   }

   public Texture getTexture(ImageData dataSource, int filter) throws IOException {
      int target = 3553;
      ByteBuffer textureBuffer = dataSource.getImageBufferData();
      int textureID = createTextureID();
      TextureImpl texture = new TextureImpl("generated:" + dataSource, target, textureID);
      boolean flipped = false;
      GL.glBindTexture(target, textureID);
      int width = dataSource.getWidth();
      int height = dataSource.getHeight();
      boolean hasAlpha = dataSource.getDepth() == 32;
      texture.setTextureWidth(dataSource.getTexWidth());
      texture.setTextureHeight(dataSource.getTexHeight());
      int texWidth = texture.getTextureWidth();
      int texHeight = texture.getTextureHeight();
      int srcPixelFormat = hasAlpha ? 6408 : 6407;
      int componentCount = hasAlpha ? 4 : 3;
      texture.setWidth(width);
      texture.setHeight(height);
      texture.setAlpha(hasAlpha);
      IntBuffer temp = BufferUtils.createIntBuffer(16);
      GL.glGetInteger(3379, temp);
      int max = temp.get(0);
      if (texWidth <= max && texHeight <= max) {
         if (this.holdTextureData) {
            texture.setTextureData(srcPixelFormat, componentCount, filter, filter, textureBuffer);
         }

         GL.glTexParameteri(target, 10241, filter);
         GL.glTexParameteri(target, 10240, filter);
         GL.glTexImage2D(target, 0, this.dstPixelFormat, get2Fold(width), get2Fold(height), 0, srcPixelFormat, 5121, textureBuffer);
         return texture;
      } else {
         throw new IOException("Attempt to allocate a texture to big for the current hardware");
      }
   }

   public static int get2Fold(int fold) {
      int ret;
      for(ret = 2; ret < fold; ret *= 2) {
      }

      return ret;
   }

   public static IntBuffer createIntBuffer(int size) {
      ByteBuffer temp = ByteBuffer.allocateDirect(4 * size);
      temp.order(ByteOrder.nativeOrder());
      return temp.asIntBuffer();
   }

   public void reload() {
      Iterator texs = this.texturesLinear.values().iterator();

      while(texs.hasNext()) {
         ((TextureImpl)texs.next()).reload();
      }

      texs = this.texturesNearest.values().iterator();

      while(texs.hasNext()) {
         ((TextureImpl)texs.next()).reload();
      }

   }

   public int reload(TextureImpl texture, int srcPixelFormat, int componentCount, int minFilter, int magFilter, ByteBuffer textureBuffer) {
      int target = 3553;
      int textureID = createTextureID();
      GL.glBindTexture(target, textureID);
      GL.glTexParameteri(target, 10241, minFilter);
      GL.glTexParameteri(target, 10240, magFilter);
      GL.glTexImage2D(target, 0, this.dstPixelFormat, texture.getTextureWidth(), texture.getTextureHeight(), 0, srcPixelFormat, 5121, textureBuffer);
      return textureID;
   }
}
