package common.grid;

public interface GridData<T> {
  T getValue(int row, int col);
  T getValue(int index);
}
