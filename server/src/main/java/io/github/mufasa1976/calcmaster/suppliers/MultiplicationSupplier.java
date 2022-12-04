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
                                             .map(maxMultiplicand -> properties.maxProduct() / maxMultiplicand == 0 ? properties.maxProduct() : Math.min(maxMultiplicand, properties.maxProduct()))
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
    int multiplier = multipliers.get(random.nextInt(multipliers.size()));
    int multiplicand = multiplicands.get(random.nextInt(multiplicands.size()));
    if (multiplier < multiplicand && CollectionUtils.isEmpty(properties.fixedMultiplicands())) {
      int oldMultiplier = multiplier;
      multiplier = multiplicand;
      multiplicand = oldMultiplier;
    }
    return Calculation.builder()
                      .type(Calculation.Type.CALCULATION)
                      .operand1(multiplier)
                      .operator(OPERATOR)
                      .operand2(multiplicand)
                      .result(multiplier * multiplicand)
                      .hiddenField(getRandomHiddenField())
                      .build();
  }
}
