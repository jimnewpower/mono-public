package common.grid;

import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.function.DoubleUnaryOperator;

import common.dval.Dval;

public class GridManipulator extends RecursiveTask<MutableGridData<Double>> {
  public static MutableGridData<Double> run(
    DoubleUnaryOperator operator,
    DoubleGrid inputGrid
  ) {
    ForkJoinPool pool = ForkJoinPool.commonPool();
    int nProc = pool.getParallelism();
    long size = inputGrid.size();
    int maxItemsPerProcess = (int) ((double) size / (double) nProc / 2.0);
    if (maxItemsPerProcess < 1)
      maxItemsPerProcess = 1;

    GridManipulator gridManipulator = GridManipulator.create(
        operator, 
        inputGrid,
        maxItemsPerProcess);
    
    MutableGridData<Double> result = pool.invoke(gridManipulator);
    return result;
  }
  
  public static GridManipulator create(
    DoubleUnaryOperator operator,
    DoubleGrid inputGrid,
    int maxItemsPerProcess
  ) {
    MutableGridData<Double> resultGrid = 
        MutableDoubleGrid.create(inputGrid, Dval.DVAL_DOUBLE);
    return new GridManipulator(
      operator, 
      inputGrid, 
      resultGrid, 
      0/* startIndex */, 
      (int)inputGrid.size() - 1/* endIndex */,
      maxItemsPerProcess/* max */
    );
  }
  
  private final DoubleUnaryOperator operator;
  private final DoubleGrid inputGrid;
  private final MutableGridData<Double> resultGrid;
  private final int start;
  private final int end;
  private final int max;

  private GridManipulator(
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
  protected MutableGridData<Double> compute() {
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

    return resultGrid;
  }

  private MutableGridData<Double> computeDirectly() {
    for (int index = start; index <= end; index++) {
      resultGrid.setValue(index, operator.applyAsDouble(inputGrid.getValue(index)));
    }
    return resultGrid;
  }
}
