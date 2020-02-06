package common.grid;

public interface GridIndexer {
  int getCellIndex(int row, int col);
  int getRow(int cellIndex);
  int getColumn(int cellIndex);
  
  public static GridIndexer rowMajorWithOriginInLowerLeftCorner(Grid grid) {
    return new GridIndexer() {
      @Override
      public int getCellIndex(int row, int col) {
        if (col < 0 || col >= grid.getNColumns()) {
          throw new ArrayIndexOutOfBoundsException("col=" + col + " but nCols=" + grid.getNColumns());
        }
        if (row < 0 || row >= grid.getNRows()) {
          throw new ArrayIndexOutOfBoundsException("row=" + row + " but nRows=" + grid.getNRows());
        }
        return ((row) * grid.getNColumns()) + col;
      }

      @Override
      public int getRow(int cellIndex) {
        if (cellIndex < 0 || cellIndex >= grid.getNColumns() * grid.getNRows())
          return -1;

        if (cellIndex < grid.getNColumns())
          return 0;

        return cellIndex / grid.getNColumns();
      }

      @Override
      public int getColumn(int cellIndex) {
        if (cellIndex < 0 || cellIndex >= grid.getNColumns() * grid.getNRows())
          return -1;

        if (cellIndex < grid.getNColumns())
          return cellIndex;

        return cellIndex % grid.getNColumns();
      }
    };
  }
  
  public static GridIndexer rowMajorWithOriginInUpperLeftCorner(Grid grid) {
    return new GridIndexer() {
      @Override
      public int getCellIndex(int row, int col) {
        if (col < 0 || col >= grid.getNColumns()) {
          throw new ArrayIndexOutOfBoundsException("col=" + col + " but nCols=" + grid.getNColumns());
        }
        if (row < 0 || row >= grid.getNRows()) {
          throw new ArrayIndexOutOfBoundsException("row=" + row + " but nRows=" + grid.getNRows());
        }
        return ((grid.getNRows()-1-row) * grid.getNColumns()) + col;
      }

      @Override
      public int getRow(int cellIndex) {
        if (cellIndex < 0 || cellIndex >= grid.getNColumns() * grid.getNRows())
          return -1;

        if (cellIndex < grid.getNColumns())
          return 0;

        return grid.getNRows() - 1 - (cellIndex / grid.getNColumns());
      }

      @Override
      public int getColumn(int cellIndex) {
        if (cellIndex < 0 || cellIndex >= grid.getNColumns() * grid.getNRows())
          return -1;

        if (cellIndex < grid.getNColumns())
          return cellIndex;

        return cellIndex % grid.getNColumns();
      }
    };
  }
}
