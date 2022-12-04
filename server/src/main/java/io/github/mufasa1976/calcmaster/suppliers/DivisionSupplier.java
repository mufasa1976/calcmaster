package io.github.mufasa1976.calcmaster.suppliers;

import lombok.extern.slf4j.Slf4j;
import io.github.mufasa1976.calcmaster.enums.HiddenField;
import io.github.mufasa1976.calcmaster.dtos.Calculation;
import io.github.mufasa1976.calcmaster.records.DivisionProperties;

import java.util.*;
import java.util.stream.IntStream;

@Slf4j
public class DivisionSupplier extends AbstractCalculationSupplier {
  private static final String OPERATOR = ":";
  private final DivisionProperties properties;

  private final List<Integer> divisors = new ArrayList<>();

  private final List<Integer> quotients = new ArrayList<>();

  public DivisionSupplier(Random random, boolean toggleHide, int maxTriesToGetDistinctOperationTuples, DivisionProperties properties) {
    super(random, toggleHide, maxTriesToGetDistinctOperationTuples);
    this.properties = properties;
  }

  @Override
  public void init() {
    if (isFixedDivisorsNotEmpty()) {
      properties.fixedDivisors()
                .stream()
                .filter(divisor -> divisor != 0)
                .sorted()
                .forEach(divisors::add);

      final int maxQuotient = divisors.stream()
                                      .max(Integer::compare)
                                      .map(maxDivisor -> properties.maxDividend() / maxDivisor)
                                      .orElse(Double.valueOf(Math.round(Math.sqrt(properties.maxDividend()))).intValue());
      IntStream.rangeClosed(1, maxQuotient)
               .filter(canBeIncluded(properties.exclusions()))
               .sorted()
               .forEach(quotients::add);
    } else {
      final int maxDivisor = Double.valueOf(Math.round(Math.sqrt(properties.maxDividend()))).intValue();
      IntStream.rangeClosed(1, maxDivisor)
               .filter(canBeIncluded(properties.exclusions()))
               .sorted()
               .forEach(divisors::add);

      int maxQuotient = maxDivisor;
      if (maxDivisor * maxQuotient > properties.maxDividend()) {
        maxQuotient -= 1;
      }
      IntStream.rangeClosed(0, maxQuotient)
               .filter(canBeIncluded(properties.exclusions()))
               .sorted()
               .forEach(quotients::add);
    }
  }

  private boolean isFixedDivisorsNotEmpty() {
    return Optional.ofNullable(properties.fixedDivisors())
                   .stream()
                   .flatMap(Collection::stream)
                   .filter(value -> value != 0)
                   .toList().size() > 0;
  }

  @Override
  public Calculation getInternal() {
    final int divisor = divisors.get(random.nextInt(divisors.size()));

    final int remainder = properties.maxRemainder() > 0
        ? random.nextInt(Math.min(properties.maxRemainder() + 1, divisor))
        : 0;

    int quotient = quotients.get(random.nextInt(quotients.size()));
    if (quotient * divisor + remainder > properties.maxDividend()) {
      quotient -= 1;
    }

    int dividend = quotient > 0 ? (quotient * divisor) + remainder : 0;
    if (dividend > properties.maxDividend()) {
      dividend = properties.maxDividend();
    }

    return Calculation.builder()
                      .type(Calculation.Type.CALCULATION)
                      .operand1(dividend)
                      .operator(OPERATOR)
                      .operand2(divisor)
                      .result(dividend / divisor)
                      .remainder((long) dividend % divisor)
                      .hiddenField(HiddenField.RESULT)
                      .build();
  }
}
