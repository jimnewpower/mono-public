package common.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import common.bounds.Bounds2D;
import common.dval.Dval;

public class GridTest {
  @Test
  void gridTest() {
    int rows = 5;
    int columns = 11;
    Grid grid = Grid.create(rows, columns, mockBounds());
    assertEquals(rows, grid.getNRows());
    assertEquals(columns, grid.getNColumns());
    assertEquals(rows * columns, grid.size());
    assertEquals(25, grid.getCellHeight(), 1e-10);
    assertEquals(10, grid.getCellWidth(), 1e-10);
  }

  @Test
  void rowsTooSmall() {
    assertThrows(
        IllegalArgumentException.class, 
        () -> Grid.create(1 /* rows */, 10/* columns */, mockBounds()));
  }

  @Test
  void rowsDval() {
    assertThrows(
        IllegalArgumentException.class, 
        () -> Grid.create(Dval.DVAL_INT /* rows */, 10/* columns */, mockBounds()));
  }

  @Test
  void columnsTooSmall() {
    assertThrows(
        IllegalArgumentException.class, 
        () -> Grid.create(10 /* rows */, 1/* columns */, mockBounds()));
  }

  @Test
  void columnsDval() {
    assertThrows(
        IllegalArgumentException.class, 
        () -> Grid.create(10 /* rows */, Dval.DVAL_INT /* columns */, mockBounds()));
  }

  @Test
  void nullBounds() {
    assertThrows(
        NullPointerException.class, 
        () -> Grid.create(10 /* rows */, 10/* columns */, null /* bounds */));
  }
  
  private static Bounds2D mockBounds() {
    return Bounds2D.create(0/* minX */, 100/* maxX */, 0/* minY */, 100/* maxY */);
  }
}
