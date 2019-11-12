package common.grid;

import common.geometry.Spatial;

public interface Grid extends Spatial {
  int getNRows();
  int getNColumns();
  long size();
  double getCellHeight();
  double getCellWidth();
}
