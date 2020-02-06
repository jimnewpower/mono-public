package common.grid;

import java.util.Objects;
import java.util.concurrent.RecursiveTask;
import java.util.function.DoubleUnaryOperator;

public class GridManipulator extends RecursiveTask<Boolean> {
  private final DoubleUnaryOperator operator;
  private final DoubleGrid inputGrid;
  private final MutableGridData<Double> resultGrid;
  private final int start;
  private final int end;
  private final int max;

  public GridManipulator(
    DoubleUnaryOperator operator,
    DoubleGrid inputGrid,
    MutableGridData<Double> resultGrid,
    int startIndex,
    int endIndex,
    int max
  ) {
    this.operator = Objects.requireNonNull(operator);
    this.inputGrid = Objects.requireNonNull(inputGrid);
    this.resultGrid = Objects.requireNonNull(resultGrid);
    this.start = startIndex;
    this.end = endIndex;
    this.max = max;
  }

  @Override
  protected Boolean compute() {
    int size = 1 + end - start;
    if (size <= max) {
      return computeDirectly();
    }

    int mid = start + (end - start) / 2;
    if (mid-1 <= start || mid >= end) {
      return computeDirectly();
    }

    invokeAll(
      new GridManipulator(
        operator,
        inputGrid,
        resultGrid,
        start,
        mid-1,
        max
      ),
      new GridManipulator(
        operator,
        inputGrid,
        resultGrid,
        mid,
        end,
        max
      )
    );

    return Boolean.TRUE;
  }

  private Boolean computeDirectly() {
    for (int index = start; index <= end; index++) {
      resultGrid.setValue(index, operator.applyAsDouble(inputGrid.getValue(index)));
    }
    return Boolean.TRUE;
  }
}
