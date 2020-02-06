package common.grid;

import java.util.Objects;

import common.bounds.Bounds2D;

public class DoubleGrid implements Grid, GridData<Double> {
  public static DoubleGrid create(Grid geometry, double[] values) {
    return new DoubleGrid(geometry, values);
  }

  public static DoubleGrid create(Grid geometry, double[] values, GridIndexer indexer) {
    return new DoubleGrid(geometry, values, indexer);
  }

  protected Grid geometry;
  protected GridIndexer indexer;
  protected double[] values;
  
  protected DoubleGrid(Grid geometry, double[] values) {
    this.geometry = Objects.requireNonNull(geometry);
    this.values = Objects.requireNonNull(values);
    if (values.length != geometry.size())
      throw new IllegalArgumentException(
          "values length (" + values.length + ") must equal geometry size (" + geometry.size() + ")");
    this.indexer = GridIndexer.rowMajorWithOriginInLowerLeftCorner(geometry);
  }

  protected DoubleGrid(Grid geometry, double[] values, GridIndexer indexer) {
    this.geometry = Objects.requireNonNull(geometry);
    this.values = Objects.requireNonNull(values);
    if (values.length != geometry.size())
      throw new IllegalArgumentException(
          "values length (" + values.length + ") must equal geometry size (" + geometry.size() + ")");
    this.indexer = Objects.requireNonNull(indexer);
  }

  @Override
  public Bounds2D getBounds() {
    return geometry.getBounds();
  }

  @Override
  public int getNRows() {
    return geometry.getNRows();
  }

  @Override
  public int getNColumns() {
    return geometry.getNColumns();
  }

  @Override
  public long size() {
    return geometry.size();
  }

  @Override
  public double getCellHeight() {
    return geometry.getCellHeight();
  }

  @Override
  public double getCellWidth() {
    return geometry.getCellWidth();
  }

  @Override
  public Double getValue(int row, int col) {
    return values[indexer.getCellIndex(row, col)];
  }

  @Override
  public Double getValue(int index) {
    return values[index];
  }
}
