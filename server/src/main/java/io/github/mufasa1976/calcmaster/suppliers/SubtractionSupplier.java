package io.github.mufasa1976.calcmaster.suppliers;

import io.github.mufasa1976.calcmaster.dtos.Calculation;
import io.github.mufasa1976.calcmaster.records.SubtractionProperties;

import java.util.Random;

import static io.github.mufasa1976.calcmaster.records.CalculationProperties.UNLIMITED_TRANSGRESSIONS;

public class SubtractionSupplier extends AbstractCalculationSupplier {
  private static final String OPERATOR = "-";

  private final SubtractionProperties properties;

  public SubtractionSupplier(Random random, boolean toggleHide, int maxTriesToGetDistinctOperationTuples, SubtractionProperties properties) {
    super(random, toggleHide, maxTriesToGetDistinctOperationTuples);
    this.properties = properties;
  }

  @Override
  public Calculation getInternal() {
    final var unlimitedTransgressions = properties.transgression() == UNLIMITED_TRANSGRESSIONS || properties.transgression() == (1 << (int) Math.log10(properties.maxDifference())) - 1;
    final var operands = unlimitedTransgressions ? getOperandsWithoutAnyTransgression() : getOperandsWithTransgression();
    return Calculation.builder()
                      .type(Calculation.Type.CALCULATION)
                      .operand1(operands[0])
                      .operator(OPERATOR)
                      .operand2(operands[1])
                      .result(operands[0] - operands[1])
                      .hiddenField(getRandomHiddenField())
                      .build();
  }

  private long[] getOperandsWithoutAnyTransgression() {
    final var lowerBoundSubtrahend = properties.subtrahendRounding() > 1 ? properties.subtrahendRounding() : properties.includeZeroOnOperand() ? 0 : 1;
    var subtrahend = (random.nextInt(lowerBoundSubtrahend, properties.maxDifference()) / (properties.subtrahendRounding() > 0 ? properties.subtrahendRounding() : 1)) * properties.subtrahendRounding();
    var minuend = random.nextInt(subtrahend, properties.maxDifference() + 1);
    if (properties.minDifference() > 0) {
      subtrahend = (random.nextInt(lowerBoundSubtrahend, properties.maxDifference() - properties.minDifference()) / (properties.subtrahendRounding() > 0 ? properties.subtrahendRounding() : 1))
          * properties.subtrahendRounding();
      minuend = random.nextInt(subtrahend + properties.minDifference(), properties.maxDifference() + 1);
    }
    return new long[] {minuend, subtrahend};
  }

  private long[] getOperandsWithTransgression() {
    final int digits = (int) Math.log10(properties.maxDifference());
    if (digits < 1) {
      return getOperandsWithoutAnyTransgression();
    }

    final int[] operand1 = new int[digits];
    final int[] operand2 = new int[digits];
    int digit = 0;
    int remainderOfPreviousDigit = 0;
    do {
      boolean transgression = (properties.transgression() & (1 << digit)) == (1 << digit);
      int bound = 10;
      if (properties.maxDifference() <= Math.pow(10, digit + 1)) {
        bound = properties.maxDifference() / (int) Math.pow(10, Math.ceil(Math.log10(properties.maxDifference())));
        transgression = true;
      }
      bound -= transgression ? remainderOfPreviousDigit : 0;
      if (bound > 0) {
        operand1[operand1.length - digit - 1] = random.nextInt(bound);
        if (!transgression) {
          operand2[operand2.length - digit - 1] = random.nextInt(bound);
        } else if (operand1[operand1.length - digit - 1] + remainderOfPreviousDigit < 10) {
          operand2[operand2.length - digit - 1] = random.nextInt(10 - operand1[operand1.length - digit - 1] - remainderOfPreviousDigit);
        }
      }
      remainderOfPreviousDigit = (operand1[operand1.length - digit - 1] + operand2[operand2.length - digit - 1]) / 10;
    } while (++digit < digits);

    final long[] operands = new long[] {getValue(operand1), getValue(operand2)};
    if (operands[0] < operands[1]) {
      return new long[] {operands[0] + operands[1], operands[1]};
    }
    return new long[] {operands[0] + operands[1], operands[0]};
  }
}
