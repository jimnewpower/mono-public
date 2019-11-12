package common.geometry;

public class Coordinate {
  public final double x;
  public final double y;
  
  public static Coordinate of(double x, double y) {
    return new Coordinate(x, y);
  }
  
  Coordinate(double x, double y) {
    this.x = x;
    this.y = y;
  }
}
