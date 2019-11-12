package common.bounds;

@FunctionalInterface
interface BoundsMutator {
  public void setBounds(double min, double max);
}