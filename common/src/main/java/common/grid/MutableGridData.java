package common.grid;

public interface MutableGridData<T> extends GridData<T> {
  void setValue(int row, int col, T data);
  void setValue(int cellIndex, T data);
}
