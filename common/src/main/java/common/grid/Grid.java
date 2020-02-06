package common.grid;

import java.util.Objects;

import common.bounds.Bounds2D;
import common.dval.Dval;
import common.geometry.Spatial;

public interface Grid extends Spatial {
  int getNRows();
  int getNColumns();
  long size();
  double getCellHeight();
  double getCellWidth();
  
  public static Grid create(int rows, int columns, Bounds2D bounds) {
    if (rows <= 1)
      throw new IllegalArgumentException("n rows must be > 1.");
    if (columns <= 1)
      throw new IllegalArgumentException("n columns must be > 1.");
    if (Dval.isDval(rows))
      throw new IllegalArgumentException("n rows invalid (Dval).");
    if (Dval.isDval(columns))
      throw new IllegalArgumentException("n columns invalid (Dval).");
    
    Objects.requireNonNull(bounds);
    if (!bounds.isValid())
      throw new IllegalArgumentException("invalid bounds:" + bounds);
    
    return new Grid() {
      @Override
      public Bounds2D getBounds() {
        return bounds;
      }

      @Override
      public int getNRows() {
        return rows;
      }

      @Override
      public int getNColumns() {
        return columns;
      }

      @Override
      public long size() {
        return rows * columns;
      }

      @Override
      public double getCellHeight() {
        return bounds.getHeight() / (rows-1);
      }

      @Override
      public double getCellWidth() {
        return bounds.getWidth() / (columns-1);
      }
    };
  }
}
