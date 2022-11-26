package io.github.mufasa1976.calcmaster.suppliers;

import io.github.mufasa1976.calcmaster.dtos.Calculation;
import io.github.mufasa1976.calcmaster.records.RoundingProperties;

import java.util.Random;

public class RoundingSupplier extends AbstractCalculationSupplier {
  private final RoundingProperties roundingProperties;

  public RoundingSupplier(Random random, boolean toggleHide, int maxTriesToGetDistinctOperationTuples, RoundingProperties roundingProperties) {
    super(random, toggleHide, maxTriesToGetDistinctOperationTuples);
    this.roundingProperties = roundingProperties;
  }

  @Override
  public void init() {

  }

  @Override
  protected Calculation getInternal() {
    return Calculation.builder()
                      .build();
  }
}
