package io.github.mufasa1976.calcmaster.suppliers;

import io.github.mufasa1976.calcmaster.dtos.Calculation;
import io.github.mufasa1976.calcmaster.records.SubtractionProperties;

import java.util.Random;

public class SubtractionSupplier extends AbstractCalculationSupplier {
  private static final String OPERATOR = "-";

  private final SubtractionProperties properties;

  public SubtractionSupplier(Random random, boolean toggleHide, int maxTriesToGetDistinctOperationTuples, SubtractionProperties properties) {
    super(random, toggleHide, maxTriesToGetDistinctOperationTuples);
    this.properties = properties;
  }

  @Override
  public Calculation getInternal() {
    final var lowerBoundSubtrahend = properties.subtrahendRounding() > 1 ? properties.subtrahendRounding() : properties.includeZeroOnOperand() ? 0 : 1;
    var subtrahend = (random.nextInt(lowerBoundSubtrahend, properties.maxDifference()) / properties.subtrahendRounding()) * properties.subtrahendRounding();
    var minuend = random.nextInt(subtrahend, properties.maxDifference() + 1);
    if (properties.minDifference() > 0) {
      subtrahend = (random.nextInt(lowerBoundSubtrahend, properties.maxDifference() - properties.minDifference()) / properties.subtrahendRounding()) * properties.subtrahendRounding();
      minuend = random.nextInt(subtrahend + properties.minDifference(), properties.maxDifference() + 1);
    }
    final var difference = minuend - subtrahend;
    return Calculation.builder()
                      .operand1(minuend)
                      .operator(OPERATOR)
                      .operand2(subtrahend)
                      .result(difference)
                      .hiddenField(getRandomHiddenField())
                      .build();
  }
}
