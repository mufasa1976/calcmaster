package io.github.mufasa1976.calcmaster.services;

import io.github.mufasa1976.calcmaster.dtos.Calculation;
import io.github.mufasa1976.calcmaster.enums.Operator;
import io.github.mufasa1976.calcmaster.enums.Unit;
import io.github.mufasa1976.calcmaster.enums.UnitPrefix;
import io.github.mufasa1976.calcmaster.records.CalculationProperties;
import io.github.mufasa1976.calcmaster.records.Calculations;
import io.github.mufasa1976.calcmaster.records.ConversionProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"spring.main.banner-mode=off", "application.max-number-of-calculations=1000000", "logging.level.io.github.mufasa1976.calcmaster=debug"})
@Slf4j
public class ConversionExercisesTest {
  private static final int NUMBER_OF_EXERCISES = 1_000_000;

  @Autowired
  private CalculationService calculationService;

  @ParameterizedTest
  @DisplayName("Conversion Exercises with different Units")
  @MethodSource
  void conversionExercises(CalculationProperties calculationProperties, Unit expectedUnit) {
    // WHEN
    final var calculationsCandidate = calculationService.createCalculations(calculationProperties, Locale.ENGLISH);

    // THEN
    assertThat(calculationsCandidate).isPresent();
    final var calculations = calculationsCandidate.orElseThrow();
    assertThat(calculations).extracting(Calculations::subheader, Calculations::verticalDisplay).contains(null, false);
    assertThat(calculations.calculations())
        .hasSize(NUMBER_OF_EXERCISES)
        .allMatch(calculation -> calculation.getType() == Calculation.Type.CONVERSION)
        .allSatisfy(toConversionUnit(expectedUnit));
  }

  static Stream<Arguments> conversionExercises() {
    return Stream.of(
        Arguments.of(Named.of("Meters", CalculationProperties.builder()
                                                             .operators(List.of(Operator.CONVERT))
                                                             .numberOfCalculations(NUMBER_OF_EXERCISES)
                                                             .conversionProperties(
                                                                 ConversionProperties.builder()
                                                                                     .unit(Unit.METER)
                                                                                     .build())
                                                             .build()), Unit.METER),
        Arguments.of(Named.of("Square Meters", CalculationProperties.builder()
                                                                    .operators(List.of(Operator.CONVERT))
                                                                    .numberOfCalculations(NUMBER_OF_EXERCISES)
                                                                    .conversionProperties(
                                                                        ConversionProperties.builder()
                                                                                            .unit(Unit.SQUARE_METER)
                                                                                            .build())
                                                                    .build()), Unit.SQUARE_METER),
        Arguments.of(Named.of("Cubic Meters", CalculationProperties.builder()
                                                                   .operators(List.of(Operator.CONVERT))
                                                                   .numberOfCalculations(NUMBER_OF_EXERCISES)
                                                                   .conversionProperties(
                                                                       ConversionProperties.builder()
                                                                                           .unit(Unit.CUBIC_METER)
                                                                                           .build())
                                                                   .build()), Unit.CUBIC_METER),
        Arguments.of(Named.of("Kilometers", CalculationProperties.builder()
                                                                 .operators(List.of(Operator.CONVERT))
                                                                 .numberOfCalculations(NUMBER_OF_EXERCISES)
                                                                 .conversionProperties(
                                                                     ConversionProperties.builder()
                                                                                         .unit(Unit.METER)
                                                                                         .withKilometers(true)
                                                                                         .build())
                                                                 .build()), Unit.METER),
        Arguments.of(Named.of("Grams", CalculationProperties.builder()
                                                            .operators(List.of(Operator.CONVERT))
                                                            .numberOfCalculations(NUMBER_OF_EXERCISES)
                                                            .conversionProperties(
                                                                ConversionProperties.builder()
                                                                                    .unit(Unit.GRAM)
                                                                                    .build())
                                                            .build()), Unit.GRAM),
        Arguments.of(Named.of("Seconds", CalculationProperties.builder()
                                                              .operators(List.of(Operator.CONVERT))
                                                              .numberOfCalculations(NUMBER_OF_EXERCISES)
                                                              .conversionProperties(
                                                                  ConversionProperties.builder()
                                                                                      .unit(Unit.SECOND)
                                                                                      .build())
                                                              .build()), Unit.SECOND),
        Arguments.of(Named.of("Litres", CalculationProperties.builder()
                                                             .operators(List.of(Operator.CONVERT))
                                                             .numberOfCalculations(NUMBER_OF_EXERCISES)
                                                             .conversionProperties(
                                                                 ConversionProperties.builder()
                                                                                     .unit(Unit.LITRE)
                                                                                     .build())
                                                             .build()), Unit.LITRE)
    );
  }

  private Consumer<Calculation> toConversionUnit(Unit unit) {
    return calculation -> {
      if (StringUtils.isNotEmpty(calculation.getTextExercise())) {
        assertThat(calculation.getTextExercise()).isEqualTo(decomposeNumber(calculation.getOperand1(), unit));
      } else {
        assertThat(calculation.getResult()).isEqualTo(Math.round(calculation.getOperand1() * calculation.getConversionFactor()));
      }
    };
  }

  private static String decomposeNumber(long value, Unit unit) {
    final var lowestUnitPrefix = Stream.of(unit.getAllowedPrefixes())
                                       .min(Comparator.comparing(UnitPrefix::getFactor))
                                       .orElse(UnitPrefix.BASE);
    final var decomposedNumber = new StringBuilder();
    for (var unitPrefix = lowestUnitPrefix; unitPrefix != null; unitPrefix = getNextHigherUnitPrefixThan(unit, unitPrefix)) {
      var conversionFactor = (int) unit.convertFactor(10.0);
      final var nextUnitPrefix = getNextHigherUnitPrefixThan(unit, unitPrefix);
      if (nextUnitPrefix != null) {
        conversionFactor = (int) unit.convertFactor(nextUnitPrefix.getFactor() / unitPrefix.getFactor());
      }
      final var digit = value - ((value / conversionFactor) * conversionFactor);
      value /= conversionFactor;
      if (digit > 0) {
        decomposedNumber.insert(0, String.format("%d%s%s", digit, unitPrefix.getPrefixSymbol() + unit.getUnitSymbol(), (decomposedNumber.isEmpty() ? StringUtils.EMPTY : " ")));
      }
    }
    return decomposedNumber.toString();
  }

  private static UnitPrefix getNextHigherUnitPrefixThan(Unit unit, UnitPrefix unitPrefix) {
    final var sortedUnitPrefixes = Stream.of(unit.getAllowedPrefixes())
                                         .sorted(Comparator.comparing(UnitPrefix::getFactor).reversed())
                                         .toArray(UnitPrefix[]::new);
    if (unitPrefix == sortedUnitPrefixes[0]) {
      return null;
    }

    int index = ArrayUtils.indexOf(sortedUnitPrefixes, unitPrefix);
    return sortedUnitPrefixes[index - 1];
  }
}
