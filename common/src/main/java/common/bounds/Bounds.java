package common.bounds;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import common.dval.Dval;
import common.math.MathUtil;

/**
 * Represents two bounding values, e.g. endpoints of a horizontal or vertical line segment.
 */
public interface Bounds {
  public double getMin();
  public double getMax();
  public double getRange();

  public default String boundsText() {
    NumberFormat nf = NumberFormat.getInstance();
    String min = Dval.isDval(getMin()) ? "Dval" : nf.format(getMin());
    String max = Dval.isDval(getMax()) ? "Dval" : nf.format(getMax());
    return "[" + min + ".." + max + "]";
  }

  public default void validateArguments(double min, double max) {
    if (!Dval.isValid.test(min))
      throw new IllegalArgumentException("min is invalid (" + min + ")");
    if (!Dval.isValid.test(max))
      throw new IllegalArgumentException("max is invalid (" + max + ")");
    if (min > max)
      throw new IllegalArgumentException("min (" + min + ") > max (" + max + ")");
  }

  public default boolean isValid() {
    return valid(getMin(), getMax());
  }

  public default boolean isValidForLogScale() {
    return valid(getMin(), getMax()) && getMin() > 0.0;
  }

  public default boolean rangeIsZero() {
    return isValid() && MathUtil.doublesEqual(getMin(), getMax());
  }

  public default boolean overlaps(Bounds other) {
    return (getMin() >= other.getMin() && getMin() <= other.getMax())
        || (getMax() >= other.getMin() && getMax() <= other.getMax())
        || (other.getMin() >= getMin() && other.getMin() <= getMax())
        || (other.getMax() >= getMin() && other.getMax() <= getMax());
  }

  public static final Bounds PERCENT = Bounds.of(0, 100);
  public static final Bounds DEGREES = Bounds.of(0, 360);
  public static final Bounds RGB_8_BIT = Bounds.of(0, 255);

  public static Bounds of(double min, double max) {
    return immutable(min, max);
  }

  public static Bounds immutable(double min, double max) {
    return ImmutableBounds.of(min, max);
  }

  public static Bounds empty() {
    return EmptyBounds.create();
  }

  public static Bounds nullBounds() {
    return new NullBounds();
  }

  public static Bounds expand(Bounds original, double[] values) {
    Bounds arrayBounds = of(values);
    if (!arrayBounds.isValid())
      return original;

    if (!original.isValid())
      return arrayBounds;

    Bounds bounds = Bounds.of(original.getMin(), original.getMax());
    for (double value : values)
      bounds = expand(bounds, value);
    return bounds;
  }

  public static Bounds expand(Bounds original, double value) {
    double min = Math.min(original.getMin(), value);
    double max = Math.max(original.getMax(), value);
    return of(min, max);
  }
  
  public static Bounds of(double[] arrayParam) {
    double[] array = Objects.requireNonNull(arrayParam, "array cannot be null");
    if (arrayParam.length == 0)
      return Bounds.nullBounds();

    DoubleSummaryStatistics stats = Arrays.stream(array).filter(Dval.isValid).summaryStatistics();
    double min = stats.getMin();
    double max = stats.getMax();
    if (!Bounds.valid(min, max))
      return Bounds.nullBounds();
    return immutable(min, max);
  }

  public static Bounds of(Collection<Double> collectionParam) {
    Collection<Double> collection = Objects.requireNonNull(collectionParam);
    if (collection.size() == 0)
      return new NullBounds();
    DoubleSummaryStatistics stats = collection.stream()
      .filter(Dval.VALID_DOUBLE_BOXED)
      .mapToDouble(d -> d.doubleValue())
      .summaryStatistics();
    return ImmutableBounds.of(stats.getMin(), stats.getMax());
  }

  public static boolean valid(double min, double max) {
    if (!Dval.isValid.test(min))
      return false;
    if (!Dval.isValid.test(max))
      return false;
    if (min > max)
      return false;
    return true;
  }

  /**
   * Determine the common bounds for a set of bounds
   *
   * @param all set of bounds
   * @return new instance of Bounds that represents the least common bounds for all
   */
  public static Bounds common(Bounds...all) {
    Objects.requireNonNull(all);
    if (all.length == 0)
      throw new IllegalArgumentException();

    if (!allOverlap(all))
      throw new IllegalArgumentException("not all bounds overlap");

    OptionalDouble highestMin =
        Arrays.stream(all).flatMapToDouble(b -> DoubleStream.of(b.getMin())).max();
    OptionalDouble lowestMax =
        Arrays.stream(all).flatMapToDouble(b -> DoubleStream.of(b.getMax())).min();
    return of(highestMin.getAsDouble(), lowestMax.getAsDouble());
  }

  static boolean allOverlap(Bounds...all) {
    Objects.requireNonNull(all);
    if (all.length == 0)
      throw new IllegalArgumentException();

    for (Bounds bounds : all) {
      if (!Arrays.stream(all).allMatch(b -> b.overlaps(bounds)))
        return false;
    }
    return true;
  }

  public static List<Bounds> mergeValid(List<Bounds> all) {
    Objects.requireNonNull(all);

    /* create copy of list as mutable bounds */
    List<MutableBounds> sorted = all.stream()
        .filter(b -> b.isValid())
        .map(b -> MutableBounds.of(b.getMin(), b.getMax()))
        .collect(Collectors.toList());

    /* sort copy by bounds min */
    Collections.sort(sorted, new Comparator<Bounds>() {
      @Override
      public int compare(Bounds b1, Bounds b2) {
        return Double.compare(b1.getMin(), b2.getMin());
      }
    });

    /* merge overlapping bounds */
    List<MutableBounds> merged = new ArrayList<>();
    merged.add(sorted.get(0));
    for (MutableBounds current : sorted) {
      MutableBounds last = merged.get(merged.size() - 1);
      if (current.getMin() <= last.getMax()) {
        last.setBounds(last.getMin(), Math.max(last.getMax(), current.getMax()));
      } else {
        merged.add(current);
      }
    }

    /* return merged list of immutable bounds */
    return merged.stream()
        .map(b -> immutable(b.getMin(), b.getMax()))
        .collect(Collectors.toList());
  }

  /**
   * Determine a discrete bin number for a value, given nBins for range; useful for
   * histograms, color scales, value windows, etc.
   *
   * @param value value
   * @param nBins number of bins for the range
   * @return the bin, if value is within range, -1 otherwise
   */
  public default int getBin(double value, int nBins) {
    if (nBins <= 0 || Dval.isDval(nBins))
      return -1;
    if (value < getMin() || value > getMax())
      return -1;
    int bin = (int)Math.floor(getFractionBetween(value) * (nBins));
    return Math.max(0, Math.min(nBins-1, bin));
  }

  /**
   * Get fraction between bounds for value
   *
   * @param value value
   * @return fraction between bounds endpoints, or Dval if value not within bounds
   */
  public default double getFractionBetween(double value) {
    if (value < getMin() || value > getMax())
      return Dval.DVAL_DOUBLE;
    if (MathUtil.doublesEqual(getMin(), getMax())) {
      if (MathUtil.doublesEqual(getMin(), value))
        return 0.0;
      return Dval.DVAL_DOUBLE;
    }
    return (value - getMin()) / getRange();
  }

  public default String format() {
    return String.format(
      "min=%s max=%s range=%s",
      Dval.isDval(getMin()) ? "Dval" : String.format("%10.3f", getMin()),
      Dval.isDval(getMax()) ? "Dval" : String.format("%10.3f", getMax()),
      Dval.isDval(getRange()) ? "Dval" : String.format("%10.3f", getRange())
    );
  }

  public default boolean isNull() {
    return Dval.isDval(getMin()) && Dval.isDval(getMax()) && !isValid();
  }

  public default void expandTo(@SuppressWarnings("unused") double value) {
    throw new IllegalStateException("Bounds is immutable");
  }

  public default boolean contains(double value) {
    if (value >= getMin() && value <= getMax())
      return true;
    return false;
  }

  public default double bound(double value) {
    return Math.min(getMax(), Math.max(getMin(), value));
  }
  
  public default int bound(int value) {
    return (int)Math.min(getMax(), Math.max(getMin(), value));
  }
}
