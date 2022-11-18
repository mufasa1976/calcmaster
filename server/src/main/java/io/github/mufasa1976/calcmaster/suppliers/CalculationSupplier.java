package io.github.mufasa1976.calcmaster.suppliers;

import org.apache.commons.collections4.CollectionUtils;
import io.github.mufasa1976.calcmaster.dtos.Calculation;

import java.util.Collection;
import java.util.function.IntPredicate;
import java.util.function.Supplier;

public interface CalculationSupplier extends Supplier<Calculation> {
  void init();

  default IntPredicate canBeIncluded(Collection<Integer> excludedNumbers) {
    return value -> CollectionUtils.isEmpty(excludedNumbers) || !excludedNumbers.contains(value);
  }
}
