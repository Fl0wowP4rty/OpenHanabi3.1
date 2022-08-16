package me.theresa.fontRenderer.font.geom;

public class Point extends Shape {
   public Point(float x, float y) {
      this.x = x;
      this.y = y;
      this.checkPoints();
   }

   public Shape transform(Transform transform) {
      float[] result = new float[this.points.length];
      transform.transform(this.points, 0, result, 0, this.points.length / 2);
      return new Point(this.points[0], this.points[1]);
   }

   protected void createPoints() {
      this.points = new float[2];
      this.points[0] = this.getX();
      this.points[1] = this.getY();
      this.maxX = this.x;
      this.maxY = this.y;
      this.minX = this.x;
      this.minY = this.y;
      this.findCenter();
      this.calculateRadius();
   }

   protected void findCenter() {
      this.center = new float[2];
      this.center[0] = this.points[0];
      this.center[1] = this.points[1];
   }

   protected void calculateRadius() {
      this.boundingCircleRadius = 0.0F;
   }
}
