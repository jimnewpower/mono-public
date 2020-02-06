package common.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import common.bounds.Bounds2D;

public class GridManipulatorTest {
  @Test
  public void testGridManipulator() {
    int rows = 4;
    int columns = 3;
    Grid grid = Grid.create(rows, columns, mockBounds());
    
    double[] values = new double[(int)grid.size()];
    for (int index = 0; index < grid.size(); index++) {
      values[index] = index;
    }
    DoubleGrid doubleGrid = DoubleGrid.create(grid, values);
    assertNotNull("doubleGrid", doubleGrid);
    
    for (int index = 0; index < grid.size(); index++) {
      double value = doubleGrid.getValue(index);
      assertEquals((double)index, value, 1e-5);
    }
    
    MutableGridData<Double> result = GridManipulator
        .run((d) -> { return d * 2; }, doubleGrid);
    
    assertNotNull("result", result);
    for (int index = 0; index < grid.size(); index++) {
      double value = result.getValue(index);
      assertEquals((double)index * 2, value, 1e-5);
    }
  }
  
  private static Bounds2D mockBounds() {
    return Bounds2D.create(0/* minX */, 100/* maxX */, 0/* minY */, 100/* maxY */);
  }
}
