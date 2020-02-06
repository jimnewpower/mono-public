package common.grid;

public class MutableDoubleGrid extends DoubleGrid implements MutableGridData<Double> {
  public static MutableDoubleGrid create(Grid geometry, double[] values) {
    return new MutableDoubleGrid(geometry, values);
  }

  public static MutableDoubleGrid create(Grid geometry, double[] values, GridIndexer indexer) {
    return new MutableDoubleGrid(geometry, values, indexer);
  }

  private MutableDoubleGrid(Grid geometry, double[] values) {
    super(geometry, values);
  }

  private MutableDoubleGrid(Grid geometry, double[] values, GridIndexer indexer) {
    super(geometry, values, indexer);
  }

  @Override
  public void setValue(int row, int col, Double data) {
    int index = indexer.getCellIndex(row, col);
    if (index < 0 || index >= size())
      throw new ArrayIndexOutOfBoundsException("invalid row/col:"+row+"/"+col);
    values[index] = data;
  }

  @Override
  public void setValue(int cellIndex, Double data) {
    values[cellIndex] = data;
  }
}
