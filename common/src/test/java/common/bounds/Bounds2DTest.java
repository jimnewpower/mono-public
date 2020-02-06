package common.bounds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import common.dval.Dval;
import common.geometry.Coordinate;

/**
 *
 * @author Jim Newpower
 */
public class Bounds2DTest {
  private double minX = 0.0;
  private double maxX = 1.0;
  private double minY = -1.0;
  private double maxY = 1.0;
  private Bounds2D simpleBounds;

  @Before
  public void setUp() {
    simpleBounds = Bounds2D.create(minX, maxX, minY, maxY);
  }

  @Test public void fromArrays() {
    Bounds2D bounds = Bounds2D.from(
        new double[] { -32, -16, -8, -4, -2, -64, -128 }, 
        new double[] { 5000, 5000, 5000, 5000, 5000, 5000, 5000 }
      );
    assertEquals(-128, bounds.getMinX(), 1e-10);
    assertEquals(-2, bounds.getMaxX(), 1e-10);
    assertEquals(5000, bounds.getMinY(), 1e-10);
    assertEquals(5000, bounds.getMaxY(), 1e-10);
  }

  @Test public void fromCoordinates() {
    Bounds2D bounds = Bounds2D.from(new Coordinate[] {
        Coordinate.of(4, 400),
        Coordinate.of(1, 100),
        Coordinate.of(2, 200),
        Coordinate.of(9, 900),
        Coordinate.of(5, 500)
    });
    assertEquals(1, bounds.getMinX(), 1e-10);
    assertEquals(9, bounds.getMaxX(), 1e-10);
    assertEquals(100, bounds.getMinY(), 1e-10);
    assertEquals(900, bounds.getMaxY(), 1e-10);
  }
  
  @Test
  public void testValidBounds() {
    assertTrue(simpleBounds.isValid());
  }

  @Test public void testDisjoint() {
    /* disjoint bounds */
    Bounds2D bounds0 = Bounds2D.create(0, 100, 0, 100);
    Bounds2D bounds1 = Bounds2D.create(-10, -1, 101, 110);
    assertTrue(bounds0.disjoint(bounds1));
    assertTrue(bounds1.disjoint(bounds0));

    bounds0 = Bounds2D.create(0, 100, 0, 100);
    bounds1 = Bounds2D.create(250, 10000, -8500, -4000);
    assertTrue(bounds0.disjoint(bounds1));
    assertTrue(bounds1.disjoint(bounds0));

    /* not disjoint bounds */
    bounds0 = Bounds2D.create(0, 100, 0, 100);
    bounds1 = Bounds2D.create(1, 99, 1, 99);
    assertFalse(bounds0.disjoint(bounds1));
    assertFalse(bounds1.disjoint(bounds0));

    bounds0 = Bounds2D.create(0, 100, 0, 100);
    bounds1 = Bounds2D.create(50, 150, -50, 50);
    assertFalse(bounds0.disjoint(bounds1));
    assertFalse(bounds1.disjoint(bounds0));

    bounds0 = Bounds2D.create(0, 100, 0, 100);
    bounds1 = Bounds2D.create(-500, 0, 100, 250);
    assertFalse(bounds0.disjoint(bounds1));
    assertFalse(bounds1.disjoint(bounds0));
  }

  @Test
  public void testDefaultConstructor() {
    Bounds2D bounds = Bounds2D.empty();
    // ensure that the DEFAULT_VALUE rules have not changed in the Bounds2D class
    final double tol = 1e-10;
    assertEquals(EmptyBounds.DEFAULT_VALUE, bounds.getMinX(), tol);
    assertEquals(-EmptyBounds.DEFAULT_VALUE, bounds.getMaxX(), tol);
    assertEquals(EmptyBounds.DEFAULT_VALUE, bounds.getMinY(), tol);
    assertEquals(-EmptyBounds.DEFAULT_VALUE, bounds.getMaxY(), tol);
    assertFalse(bounds.isValid());
    assertTrue(bounds.isDefault());
  }

  @Test public void testContains() {
    Bounds2D bigBounds = Bounds2D.create(0, 100, 0, 100);
    Bounds2D smallBounds = Bounds2D.create(1, 99, 1, 99);
    assertTrue(bigBounds.contains(smallBounds));
    assertFalse(smallBounds.contains(bigBounds));
  }

  @Test
  public void testRatio() {
    Bounds2D bounds = Bounds2D.create(0, 10, 0, 4);
    assertEquals(2.5, bounds.ratioXY(), 0.0);
    assertEquals(0.4, bounds.ratioYX(), 0.0);

    bounds = Bounds2D.create(0, 100, 0, 100);
    assertEquals(1.0, bounds.ratioXY(), 0.0);
    assertEquals(1.0, bounds.ratioYX(), 0.0);

    /* invalid bounds should return dval */
    bounds = Bounds2D.empty();
    assertTrue(Dval.isDval(bounds.ratioXY()));
    assertTrue(Dval.isDval(bounds.ratioYX()));
  }

  @Test
  public void testPointInside() {
    // compute a point that we know is within the bounds
    double x = minX + ((maxX - minX) / 2.0);
    double y = minY + ((maxY - minY) / 2.0);
    assertTrue(simpleBounds.isPointInside(x, y));
  }

  @Test
  public void testMinMaxWidthHeight() {
    double[] xs = new double[] {0.0, 5.0, -10.0};
    double[] ys = new double[] {-30.0, 100.0, -72.0};

    Bounds2D bounds = Bounds2D.from(xs, ys);
    assertTrue(bounds.isValid());

    double minX1 = Double.MAX_VALUE;
    double maxX1 = -Double.MAX_VALUE;
    double minY1 = Double.MAX_VALUE;
    double maxY1 = -Double.MAX_VALUE;
    for (int index = 0; index < xs.length; index++) {
      if (xs[index] < minX1)
        minX1 = xs[index];
      if (xs[index] > maxX1)
        maxX1 = xs[index];
      if (ys[index] < minY1)
        minY1 = ys[index];
      if (ys[index] > maxY1)
        maxY1 = ys[index];
    }
    double width = maxX1 - minX1;
    double height = maxY1 - minY1;
    assertEquals(minX1, bounds.getMinX(), 0.0);
    assertEquals(maxX1, bounds.getMaxX(), 0.0);
    assertEquals(minY1, bounds.getMinY(), 0.0);
    assertEquals(maxY1, bounds.getMaxY(), 0.0);
    assertEquals(width, bounds.getWidth(), 0.0);
    assertEquals(height, bounds.getHeight(), 0.0);
  }

  @Test
  public void testWidthHeight() {
    assertEquals(maxX - minX, simpleBounds.getWidth(), 0.0);
    assertEquals(maxY - minY, simpleBounds.getHeight(), 0.0);
  }

  @Test
  public void testExpansion() {
    double x = -5.0;//should be new MIN x
    double y = 30.0;//should be new MAX y
    simpleBounds.expandTo(x, y);
    assertEquals(x, simpleBounds.getMinX(), 0.0);
    assertEquals(y, simpleBounds.getMaxY(), 0.0);

    x = 20.0;//should be new MAX x
    y = -10.0;//should be new MIN y
    simpleBounds.expandTo(x, y);
    assertEquals(x, simpleBounds.getMaxX(), 0.0);
    assertEquals(y, simpleBounds.getMinY(), 0.0);
    
    Bounds2D bounds = Bounds2D.empty();
    x = 4.2;
    y = 8.4;
    bounds.expandTo(x, y);
    assertEquals(x, bounds.getMinX(), 0.0);
    assertEquals(x, bounds.getMaxX(), 0.0);
    assertEquals(y, bounds.getMinY(), 0.0);
    assertEquals(y, bounds.getMaxY(), 0.0);
  }

  @Test
  public void testExpansionFromArrays() {
    Bounds2D bounds = Bounds2D.empty();
    double[] xs = new double[] { 0, 2, 4 };
    double[] ys = new double[] { 1, 1, 1 };
    bounds.expandTo(xs, ys);
    assertEquals(0, bounds.getMinX(), 1e-10);
    assertEquals(4, bounds.getMaxX(), 1e-10);
    assertEquals(1, bounds.getMinY(), 1e-10);
    assertEquals(1, bounds.getMaxY(), 1e-10);
  }
  
  @Test
  public void doNotExpandUnnecessarily() {
    Bounds2D bounds = Bounds2D.create(-50/*minX*/, 50/*maxX*/, 600/*minY*/, 900/*maxY*/);
    double[] xs = new double[] { -49.99, 49.99 };
    double[] ys = new double[] { 600.001, 899.999 };
    bounds.expandTo(xs, ys);
    assertEquals(-50, bounds.getMinX(), 1e-10);
    assertEquals(50, bounds.getMaxX(), 1e-10);
    assertEquals(600, bounds.getMinY(), 1e-10);
    assertEquals(900, bounds.getMaxY(), 1e-10);
  }
  
  @Test
  public void testExpandByPercentage() {
    Bounds2D bounds = Bounds2D.create(0.0, 10.0, 0.0, 10.0);
    bounds.expandByPercentage(20.0);
    assertEquals(-1.0, bounds.getMinX(), 0.0);
    assertEquals(-1.0, bounds.getMinY(), 0.0);
    assertEquals(11.0, bounds.getMaxX(), 0.0);
    assertEquals(11.0, bounds.getMaxY(), 0.0);

    bounds = Bounds2D.create(0.0, 10.0, 0.0, 10.0);
    bounds.expandByPercentage(-20.0);
    assertEquals(1.0, bounds.getMinX(), 0.0);
    assertEquals(9.0, bounds.getMaxX(), 0.0);
    assertEquals(1.0, bounds.getMinY(), 0.0);
    assertEquals(9.0, bounds.getMaxY(), 0.0);
  }
}