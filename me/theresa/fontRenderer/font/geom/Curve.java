package me.theresa.fontRenderer.font.geom;

public class Curve extends Shape {
   private final Vector2f p1;
   private final Vector2f c1;
   private final Vector2f c2;
   private final Vector2f p2;
   private final int segments;

   public Curve(Vector2f p1, Vector2f c1, Vector2f c2, Vector2f p2) {
      this(p1, c1, c2, p2, 20);
   }

   public Curve(Vector2f p1, Vector2f c1, Vector2f c2, Vector2f p2, int segments) {
      this.p1 = new Vector2f(p1);
      this.c1 = new Vector2f(c1);
      this.c2 = new Vector2f(c2);
      this.p2 = new Vector2f(p2);
      this.segments = segments;
      this.pointsDirty = true;
   }

   public Vector2f pointAt(float t) {
      float a = 1.0F - t;
      float f1 = a * a * a;
      float f2 = 3.0F * a * a * t;
      float f3 = 3.0F * a * t * t;
      float f4 = t * t * t;
      float nx = this.p1.x * f1 + this.c1.x * f2 + this.c2.x * f3 + this.p2.x * f4;
      float ny = this.p1.y * f1 + this.c1.y * f2 + this.c2.y * f3 + this.p2.y * f4;
      return new Vector2f(nx, ny);
   }

   protected void createPoints() {
      float step = 1.0F / (float)this.segments;
      this.points = new float[(this.segments + 1) * 2];

      for(int i = 0; i < this.segments + 1; ++i) {
         float t = (float)i * step;
         Vector2f p = this.pointAt(t);
         this.points[i * 2] = p.x;
         this.points[i * 2 + 1] = p.y;
      }

   }

   public Shape transform(Transform transform) {
      float[] pts = new float[8];
      float[] dest = new float[8];
      pts[0] = this.p1.x;
      pts[1] = this.p1.y;
      pts[2] = this.c1.x;
      pts[3] = this.c1.y;
      pts[4] = this.c2.x;
      pts[5] = this.c2.y;
      pts[6] = this.p2.x;
      pts[7] = this.p2.y;
      transform.transform(pts, 0, dest, 0, 4);
      return new Curve(new Vector2f(dest[0], dest[1]), new Vector2f(dest[2], dest[3]), new Vector2f(dest[4], dest[5]), new Vector2f(dest[6], dest[7]));
   }

   public boolean closed() {
      return false;
   }
}
