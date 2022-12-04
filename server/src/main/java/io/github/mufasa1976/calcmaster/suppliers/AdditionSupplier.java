package io.github.mufasa1976.calcmaster.suppliers;

import io.github.mufasa1976.calcmaster.dtos.Calculation;
import io.github.mufasa1976.calcmaster.records.AdditionProperties;

import java.util.Random;

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
    final var lowerBoundSecondAddend = properties.secondAddendRounding() > 1 ? properties.secondAddendRounding() : properties.includeZeroOnOperand() ? 0 : 1;
    var secondAddend = (random.nextInt(lowerBoundSecondAddend, properties.maxSum() + 1) / properties.secondAddendRounding()) * properties.secondAddendRounding();
    var sum = random.nextInt(Math.max(secondAddend, properties.minSum()), properties.maxSum() + 1);
    for (int i = 0; !properties.includeZeroOnOperand() && sum == secondAddend && i < maxTriesToFindSumOfAdditionNotEqualToSecondAddend; i++) {
      secondAddend = (random.nextInt(lowerBoundSecondAddend, properties.maxSum() + 1) / properties.secondAddendRounding()) * properties.secondAddendRounding();
      sum = random.nextInt(Math.max(secondAddend, properties.minSum()), properties.maxSum() + 1);
    }
    var firstAddend = sum - secondAddend;
    if (properties.secondAddendRounding() < 10 && firstAddend < secondAddend) {
      firstAddend = secondAddend;
      secondAddend = sum - firstAddend;
    }
    return Calculation.builder()
                      .type(Calculation.Type.CALCULATION)
                      .operand1(firstAddend)
                      .operator(OPERATOR)
                      .operand2(secondAddend)
                      .result(sum)
                      .hiddenField(getRandomHiddenField())
                      .build();
  }
}
