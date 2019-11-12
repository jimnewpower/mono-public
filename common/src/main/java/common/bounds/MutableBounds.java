package common.bounds;

/**
 * Mutable bounds
 */
class MutableBounds implements Bounds, BoundsMutator {
  private double min;
  private double max;

  static MutableBounds of(double min, double max) {
    return new MutableBounds(min, max);
  }
  
  private MutableBounds(double min, double max) {
    this.min = min;
    this.max = max;
  }
  
  @Override
  public double getMin() {
    return this.min;
  }

  @Override
  public double getMax() {
    return this.max;
  }

  @Override
  public double getRange() {
    return this.max - this.min;
  }

  @Override
  public void setBounds(double min, double max) {
    if (!Bounds.valid(min, max))
      return;
    this.min = min;
    this.max = max;
  }
}