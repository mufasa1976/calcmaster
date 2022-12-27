package io.github.mufasa1976.calcmaster.suppliers;

import io.github.mufasa1976.calcmaster.dtos.Calculation;
import io.github.mufasa1976.calcmaster.records.AdditionProperties;

import java.util.Random;

import static io.github.mufasa1976.calcmaster.records.CalculationProperties.UNLIMITED_TRANSGRESSIONS;

public class AdditionSupplier extends AbstractCalculationSupplier {
  private static final String OPERATOR = "+";

  private final AdditionProperties properties;
  private final int maxTriesToFindSumOfAdditionNotEqualToSecondAddend;

  public AdditionSupplier(Random random, boolean toggleHide, int maxTriesToGetDistinctOperationTuples, AdditionProperties properties, int maxTriesToFindSumOfAdditionNotEqualToSecondAddend) {
    super(random, toggleHide, maxTriesToGetDistinctOperationTuples);
    this.properties = properties;
    this.maxTriesToFindSumOfAdditionNotEqualToSecondAddend = maxTriesToFindSumOfAdditionNotEqualToSecondAddend;
  }

  @Override
  public Calculation getInternal() {
    final var unlimitedTransgressions = properties.transgression() == UNLIMITED_TRANSGRESSIONS || properties.transgression() == (1 << (int) Math.log10(properties.maxSum())) - 1;
    final var operands = unlimitedTransgressions ? getOperandsWithoutAnyTransgression() : getOperandsWithTransgression();
    return Calculation.builder()
                      .type(Calculation.Type.CALCULATION)
                      .operand1(operands[0])
                      .operator(OPERATOR)
                      .operand2(operands[1])
                      .result(operands[0] + operands[1])
                      .hiddenField(getRandomHiddenField())
                      .build();
  }

  private long[] getOperandsWithoutAnyTransgression() {
    final var lowerBoundSecondAddend = properties.secondAddendRounding() > 1 ? properties.secondAddendRounding() : properties.includeZeroOnOperand() ? 0 : 1;
    var secondAddend = (random.nextInt(lowerBoundSecondAddend, properties.maxSum() + 1) / (properties.secondAddendRounding() > 0 ? properties.secondAddendRounding() : 1))
        * properties.secondAddendRounding();
    var sum = random.nextInt(Math.max(secondAddend, properties.minSum()), properties.maxSum() + 1);
    for (int i = 0; !properties.includeZeroOnOperand() && sum == secondAddend && i < maxTriesToFindSumOfAdditionNotEqualToSecondAddend; i++) {
      secondAddend = (random.nextInt(lowerBoundSecondAddend, properties.maxSum() + 1) / (properties.secondAddendRounding() > 0 ? properties.secondAddendRounding() : 1))
          * properties.secondAddendRounding();
      sum = random.nextInt(Math.max(secondAddend, properties.minSum()), properties.maxSum() + 1);
    }
    var firstAddend = sum - secondAddend;
    if (properties.secondAddendRounding() < 10 && firstAddend < secondAddend) {
      firstAddend = secondAddend;
      secondAddend = sum - firstAddend;
    }
    return new long[] {firstAddend, secondAddend};
  }

  private long[] getOperandsWithTransgression() {
    final int digits = (int) Math.log10(properties.maxSum());
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
      if (properties.maxSum() <= Math.pow(10, digit + 1)) {
        bound = properties.maxSum() / (int) Math.pow(10, Math.ceil(Math.log10(properties.maxSum())));
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
      return new long[] {operands[1], operands[0]};
    }
    return operands;
  }

}
