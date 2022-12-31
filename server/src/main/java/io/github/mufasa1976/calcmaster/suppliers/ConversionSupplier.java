package io.github.mufasa1976.calcmaster.suppliers;

import io.github.mufasa1976.calcmaster.dtos.Calculation;
import io.github.mufasa1976.calcmaster.enums.Unit;
import io.github.mufasa1976.calcmaster.enums.UnitConversion;
import io.github.mufasa1976.calcmaster.enums.UnitPrefix;
import io.github.mufasa1976.calcmaster.records.ConversionProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.Random;
import java.util.stream.Stream;

@Slf4j
public class ConversionSupplier extends AbstractCalculationSupplier {
  private final ConversionProperties properties;

  private UnitPrefix lowestUnitPrefix = UnitPrefix.BASE;
  private UnitPrefix highestUnitPrefix = UnitPrefix.BASE;
  private double highestNumberForMatrixConversion = 1.0;
  private UnitPrefix[] sortedUnitPrefixes = new UnitPrefix[0];

  public ConversionSupplier(Random random, boolean toggleHide, ConversionProperties conversionProperties, int maxTriesToGetDistinctOperationTuples) {
    super(random, toggleHide, maxTriesToGetDistinctOperationTuples);
    this.properties = conversionProperties;
  }

  @Override
  public void init() {
    lowestUnitPrefix = Stream.of(properties.getUnit().getAllowedPrefixes())
                             .min(Comparator.comparing(UnitPrefix::getFactor))
                             .orElse(UnitPrefix.BASE);
    final var highestFactor = Stream.of(properties.getUnit().getAllowedPrefixes())
                                    .map(UnitPrefix::getFactor)
                                    .max(Double::compare)
                                    .orElse(1.0);
    highestUnitPrefix = Stream.of(properties.getUnit().getAllowedPrefixes())
                              .max(Comparator.comparing(UnitPrefix::getFactor))
                              .orElse(UnitPrefix.BASE);
    highestNumberForMatrixConversion = (highestFactor / lowestUnitPrefix.getFactor()) * 10.0;
    sortedUnitPrefixes = Stream.of(properties.getUnit().getAllowedPrefixes())
                               .sorted(Comparator.comparing(UnitPrefix::getFactor).reversed())
                               .toArray(UnitPrefix[]::new);
  }

  @Override
  protected Calculation getInternal() {
    return switch (getRandomUnitConversion(properties.getUnit())) {
      case WHOLE_NUMBERS -> getWholeNumberConversion(properties.getUnit());
      case MATRIX -> getMatrixConversion(properties.getUnit());
      case UPSCALE -> getUpscaleConversion(properties.getUnit());
    };
  }

  private UnitConversion getRandomUnitConversion(Unit unit) {
    return unit.getAllowedUnitConversions()[random.nextInt(0, unit.getAllowedUnitConversions().length)];
  }

  private Calculation getWholeNumberConversion(Unit unit) {
    final var fromUnitPrefix = getRandomUnitPrefix(unit, null);
    final var toUnitPrefix = getRandomUnitPrefix(unit, fromUnitPrefix);
    final var conversionFactor = fromUnitPrefix.getFactor() / toUnitPrefix.getFactor();
    final var randomValue = conversionFactor < 1
        ? Math.round(random.nextInt(1, 10) * (1 / conversionFactor))
        : random.nextInt(1, 10);
    final var result = Math.round(randomValue * conversionFactor);
    return Calculation.builder()
                      .type(Calculation.Type.CONVERSION)
                      .conversionType(UnitConversion.WHOLE_NUMBERS)
                      .operand1(randomValue)
                      .operand1Unit(fromUnitPrefix.getPrefixSymbol() + unit.getUnitSymbol())
                      .conversionFactor(conversionFactor)
                      .result(result)
                      .resultUnit(toUnitPrefix.getPrefixSymbol() + unit.getUnitSymbol())
                      .build();
  }

  private UnitPrefix getRandomUnitPrefix(Unit unit, UnitPrefix exceptUnitPrefix) {
    final var unitPrefixes = Stream.of(unit.getAllowedPrefixes())
                                   .filter(unitPrefix -> unitPrefix != exceptUnitPrefix)
                                   .toArray(UnitPrefix[]::new);
    if (unitPrefixes.length == 0) {
      return UnitPrefix.BASE;
    }
    return unitPrefixes[random.nextInt(0, unitPrefixes.length)];
  }

  private Calculation getMatrixConversion(Unit unit) {
    final var toUnitPrefix = getRandomUnitPrefix(unit, highestUnitPrefix);
    final var lowestNumber = (long) (toUnitPrefix.getFactor() / lowestUnitPrefix.getFactor());
    final var randomValue = getRandomNumber(lowestNumber);
    final var result = randomValue / lowestNumber;
    final var decomposedRandomValue = decomposeNumber(randomValue, unit, lowestUnitPrefix);
    return Calculation.builder()
                      .type(Calculation.Type.CONVERSION)
                      .conversionType(UnitConversion.MATRIX)
                      .operand1(randomValue)
                      .operand1Unit(lowestUnitPrefix.getPrefixSymbol() + unit.getUnitSymbol())
                      .textExercise(decomposedRandomValue)
                      .result(result)
                      .resultUnit(toUnitPrefix.getPrefixSymbol() + unit.getUnitSymbol())
                      .build();
  }

  private long getRandomNumber(long lowestNumber) {
    if (random.nextInt(0, 5) == 0) {
      final var randomMultiplicand = (int) Math.pow(10, random.nextInt(1, 2));
      if (lowestNumber * randomMultiplicand < highestNumberForMatrixConversion) {
        return (long) (random.nextDouble(lowestNumber * randomMultiplicand, highestNumberForMatrixConversion) / (lowestNumber * randomMultiplicand)) * (lowestNumber * randomMultiplicand);
      }
    }
    return (long) (random.nextDouble(lowestNumber, highestNumberForMatrixConversion) / lowestNumber) * lowestNumber;
  }

  private String decomposeNumber(long randomValue, Unit unit, UnitPrefix startUnitPrefix) {
    final var decomposedNumber = new StringBuilder();
    for (var unitPrefix = startUnitPrefix; unitPrefix != null; unitPrefix = getNextHigherUnitPrefixThan(unitPrefix)) {
      var conversionFactor = 10;
      final var nextUnitPrefix = getNextHigherUnitPrefixThan(unitPrefix);
      if (nextUnitPrefix == null) {
        if (randomValue > 0) {
          decomposedNumber.insert(0, String.format("%d%s%s", randomValue, unitPrefix.getPrefixSymbol() + unit.getUnitSymbol(), (decomposedNumber.isEmpty() ? StringUtils.EMPTY : " ")));
        }
        break;
      }
      conversionFactor = (int) (nextUnitPrefix.getFactor() / unitPrefix.getFactor());
      final var digit = randomValue - ((randomValue / conversionFactor) * conversionFactor);
      randomValue /= conversionFactor;
      if (digit > 0) {
        decomposedNumber.insert(0, String.format("%d%s%s", digit, unitPrefix.getPrefixSymbol() + unit.getUnitSymbol(), (decomposedNumber.isEmpty() ? StringUtils.EMPTY : " ")));
      }
    }
    return decomposedNumber.toString();
  }

  private UnitPrefix getNextHigherUnitPrefixThan(UnitPrefix unitPrefix) {
    if (unitPrefix == highestUnitPrefix) {
      return null;
    }

    int index = ArrayUtils.indexOf(sortedUnitPrefixes, unitPrefix);
    return sortedUnitPrefixes[index - 1];
  }

  private Calculation getUpscaleConversion(Unit unit) {
    final var unitPrefix = getRandomUnitPrefix(unit, UnitPrefix.BASE);
    final var value = Double.valueOf(random.nextDouble(10.0, highestNumberForMatrixConversion)).longValue();
    final var decomposedValue = decomposeNumber(value, unit, unitPrefix);
    return Calculation.builder()
                      .type(Calculation.Type.CONVERSION)
                      .conversionType(UnitConversion.UPSCALE)
                      .operand1(value)
                      .operand1Unit(unitPrefix.getPrefixSymbol() + unit.getUnitSymbol())
                      .textSolution(decomposedValue)
                      .build();
  }
}
