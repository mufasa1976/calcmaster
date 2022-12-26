package io.github.mufasa1976.calcmaster.suppliers;

import io.github.mufasa1976.calcmaster.dtos.Calculation;
import io.github.mufasa1976.calcmaster.records.MultiplicationProperties;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class MultiplicationSupplier extends AbstractCalculationSupplier {
  private static final String OPERATOR = "·";

  private final MultiplicationProperties properties;

  private final List<Integer> multipliers = new ArrayList<>();

  private final List<Integer> multiplicands = new ArrayList<>();

  public MultiplicationSupplier(Random random, boolean toggleHide, int maxTriesToGetDistinctOperationTuples, MultiplicationProperties properties) {
    super(random, toggleHide, maxTriesToGetDistinctOperationTuples);
    this.properties = properties;
  }

  @Override
  public void init() {
    if (CollectionUtils.isNotEmpty(properties.fixedMultiplicands())) {
      properties.fixedMultiplicands()
                .stream()
                .sorted()
                .forEach(multiplicands::add);

      final int maxMultiplier = multiplicands.stream()
                                             .max(Integer::compare)
                                             .map(maxMultiplicand -> properties.maxProduct() / maxMultiplicand == 0 ? properties.maxProduct() : properties.maxProduct() / maxMultiplicand)
                                             .orElse(Double.valueOf(Math.round(Math.sqrt(properties.maxProduct()))).intValue());
      IntStream.rangeClosed(0, maxMultiplier)
               .filter(canBeIncluded(properties.exclusions()))
               .sorted()
               .forEach(multipliers::add);
    } else {
      final int maxMultiplier = Double.valueOf(Math.round(Math.sqrt(properties.maxProduct()))).intValue();
      IntStream.range(0, maxMultiplier + 1)
               .filter(canBeIncluded(properties.exclusions()))
               .sorted()
               .forEach(multipliers::add);

      int maxMultiplicand = maxMultiplier;
      if (maxMultiplicand * maxMultiplier > properties.maxProduct()) {
        maxMultiplicand -= 1;
      }
      IntStream.range(0, maxMultiplicand + 1)
               .filter(canBeIncluded(properties.exclusions()))
               .sorted()
               .forEach(multiplicands::add);
    }
  }

  @Override
  public Calculation getInternal() {
    final var operands = properties.transgression() < 0 ? getOperandsWithoutAnyTransgression() : getOperandsWithTransgression();
    return Calculation.builder()
                      .type(Calculation.Type.CALCULATION)
                      .operand1(operands[0])
                      .operator(OPERATOR)
                      .operand2(operands[1])
                      .result((long) operands[0] * operands[1])
                      .hiddenField(getRandomHiddenField())
                      .build();
  }

  private long[] getOperandsWithoutAnyTransgression() {
    int multiplier = multipliers.get(random.nextInt(multipliers.size()));
    int multiplicand = multiplicands.get(random.nextInt(multiplicands.size()));
    if (multiplier < multiplicand && CollectionUtils.isEmpty(properties.fixedMultiplicands())) {
      int oldMultiplier = multiplier;
      multiplier = multiplicand;
      multiplicand = oldMultiplier;
    }
    return new long[] {multiplier, multiplicand};
  }

  private long[] getOperandsWithTransgression() {
    final int digits = (int) Math.log10(properties.maxProduct());
    if (digits < 1) {
      return getOperandsWithoutAnyTransgression();
    }

    final int[] operand1 = new int[digits];
    final int operand2 = random.nextInt(2, 10);
    int digit = 0;
    int remainderOfPreviousDigit = 0;
    do {
      boolean transgression = (properties.transgression() & (1 << digit)) != (1 << digit);
      final int bound = switch (operand2) {
        case 2 -> 4 - (remainderOfPreviousDigit / 2);
        case 3 -> 3 - ((remainderOfPreviousDigit + 2) / 3);
        case 4 -> 4 - ((remainderOfPreviousDigit + 10) / 4);
        default -> remainderOfPreviousDigit + operand2 >= 10 || properties.maxProduct() <= Math.pow(10, digit + 1) ? 1 : 2;
      };
      if (transgression) {
        operand1[operand1.length - digit - 1] = random.nextInt(bound);
      } else {
        operand1[operand1.length - digit - 1] = random.nextInt(properties.maxProduct() <= Math.pow(10, digit + 1) ? bound : 10);
      }
      remainderOfPreviousDigit = (operand1[operand1.length - digit - 1] * operand2) / 10;
    } while (++digit < digits);
    return new long[] {getValue(operand1), operand2};
  }
}
