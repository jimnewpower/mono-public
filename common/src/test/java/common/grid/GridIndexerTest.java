package common.grid;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import common.bounds.Bounds2D;

class GridIndexerTest {
  @Test
  void rowMajorWithOriginInLowerLeftCorner() {
    Bounds2D bounds = Bounds2D.create(0/* minX */, 100/* maxX */, 0/* minY */, 100/* maxY */);
    int rows = 5;
    int columns = 10;
    Grid grid = Grid.create(rows, columns, bounds);
    GridIndexer indexer = GridIndexer.rowMajorWithOriginInLowerLeftCorner(grid);
    
    int row = 0;
    int col = 0;
    assertEquals(0, indexer.getCellIndex(row, col));
    
    row = rows-1;
    col = columns-1;
    assertEquals(grid.size()-1, indexer.getCellIndex(row, col));
  }

  @Test
  void rowMajorWithOriginInUpperLeftCorner() {
    Bounds2D bounds = Bounds2D.create(0/* minX */, 100/* maxX */, 0/* minY */, 100/* maxY */);
    int rows = 5;
    int columns = 10;
    Grid grid = Grid.create(rows, columns, bounds);
    GridIndexer indexer = GridIndexer.rowMajorWithOriginInUpperLeftCorner(grid);
    
    int row = 0;
    int col = 0;
    assertEquals(40, indexer.getCellIndex(row, col));
    
    row = rows-1;
    col = columns-1;
    assertEquals(9, indexer.getCellIndex(row, col));
  }
}
