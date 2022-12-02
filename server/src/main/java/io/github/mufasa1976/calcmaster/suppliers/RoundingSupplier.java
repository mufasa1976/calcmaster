package io.github.mufasa1976.calcmaster.suppliers;

import io.github.mufasa1976.calcmaster.dtos.Calculation;
import io.github.mufasa1976.calcmaster.records.RoundingProperties;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.Random;

public class RoundingSupplier extends AbstractCalculationSupplier {
  private final MessageSource messageSource;
  private final Locale locale;
  private final RoundingProperties roundingProperties;

  public RoundingSupplier(Random random, boolean toggleHide, int maxTriesToGetDistinctOperationTuples, MessageSource messageSource, Locale locale, RoundingProperties roundingProperties) {
    super(random, toggleHide, maxTriesToGetDistinctOperationTuples);
    this.messageSource = messageSource;
    this.locale = locale;
    this.roundingProperties = roundingProperties;
  }

  @Override
  protected Calculation getInternal() {
    final var randomValue = random.nextInt((int) Math.pow(10, roundingProperties.minPower()), (int) Math.pow(10, roundingProperties.maxPower()));
    final var power = (int) Math.pow(10, random.nextInt(Math.min(getLength(randomValue), roundingProperties.minPower()), roundingProperties.maxPower()));
    final var result = (int) Math.round(Integer.valueOf(randomValue).doubleValue() / power) * power;
    return Calculation.builder()
                      .textExercise(messageSource.getMessage("rounding.exercise", new Object[] {randomValue, power}, locale))
                      .textSolution(messageSource.getMessage("rounding.solution", new Object[] {randomValue, power, result}, locale))
                      .operand1(randomValue)
                      .operand2(power)
                      .result(result)
                      .build();
  }

  private int getLength(int value) {
    int length = 0;
    for (int remainingValue = value; remainingValue > 0; length++) {
      remainingValue /= 10;
    }
    return length;
  }

  @Override
  protected boolean isCheckDuplicates() {
    return false;
  }
}
