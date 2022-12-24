package io.github.mufasa1976.calcmaster.services;

import io.github.mufasa1976.calcmaster.dtos.Calculation;
import io.github.mufasa1976.calcmaster.enums.Operator;
import io.github.mufasa1976.calcmaster.records.CalculationProperties;
import io.github.mufasa1976.calcmaster.records.Calculations;
import io.github.mufasa1976.calcmaster.records.DivisionProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"spring.main.banner-mode=off", "application.max-number-of-calculations=1000000"})
public class DivisionExercisesTest {
  private static final int NUMBER_OF_EXERCISES = 1_000_000;

  @Autowired
  private CalculationService calculationService;

  @Test
  @DisplayName("Division Exercises with initial Parameters")
  void divisionExercisesWithInitialValues() {
    // GIVEN
    final var calculationProperties =
        CalculationProperties.builder()
                             .operators(List.of(Operator.DIVIDE))
                             .numberOfCalculations(NUMBER_OF_EXERCISES)
                             .divisionProperties(
                                 DivisionProperties.builder()
                                                   .maxDividend(100)
                                                   .maxRemainder(0)
                                                   .build())
                             .build();

    // WHEN
    final var calculationsCandidate = calculationService.createCalculations(calculationProperties, Locale.ENGLISH);

    // THEN
    assertThat(calculationsCandidate).isNotNull();
    final var calculations = calculationsCandidate.blockOptional().orElseThrow();
    assertThat(calculations).extracting(Calculations::subheader, Calculations::verticalDisplay).contains(null, false);
    assertThat(calculations.calculations())
        .hasSize(NUMBER_OF_EXERCISES)
        .allMatch(calculation -> calculation.getType() == Calculation.Type.CALCULATION)
        .allMatch(calculation -> calculation.getOperand1() / calculation.getOperand2() == calculation.getResult())
        .allMatch(calculation -> calculation.getOperand1() % calculation.getOperand2() == calculation.getRemainder())
        .noneMatch(calculation -> calculation.getOperand1() % calculation.getOperand2() > 0)
        .noneMatch(calculation -> calculation.getResult() > 100);
  }

  @Test
  @DisplayName("Division Exercises with maxDividend = 1,000 and maxRemainder = 10 and exclusions = [0, 2] and fixedDivisors = [3, 7, 13]")
  void divisionExercisesWithMaxDividend1000AndMaxRemainder10AndWithoutZeroAnd2AndWithFixedDivisorTo3And7And13() {
    // GIVEN
    final var exclusions = List.of(0, 2);
    final var fixedDivisors = List.of(3, 7, 13);
    final var calculationProperties =
        CalculationProperties.builder()
                             .operators(List.of(Operator.DIVIDE))
                             .numberOfCalculations(NUMBER_OF_EXERCISES)
                             .divisionProperties(
                                 DivisionProperties.builder()
                                                   .maxDividend(1_000)
                                                   .maxRemainder(10)
                                                   .exclusions(exclusions)
                                                   .fixedDivisors(fixedDivisors)
                                                   .build())
                             .build();

    // WHEN
    final var calculationsCandidate = calculationService.createCalculations(calculationProperties, Locale.ENGLISH);

    // THEN
    assertThat(calculationsCandidate).isNotNull();
    final var calculations = calculationsCandidate.blockOptional().orElseThrow();
    assertThat(calculations).extracting(Calculations::subheader, Calculations::verticalDisplay).contains(null, false);
    assertThat(calculations.calculations())
        .hasSize(NUMBER_OF_EXERCISES)
        .allMatch(calculation -> calculation.getType() == Calculation.Type.CALCULATION)
        .allMatch(calculation -> calculation.getOperand1() / calculation.getOperand2() == calculation.getResult())
        .allMatch(calculation -> calculation.getOperand1() % calculation.getOperand2() == calculation.getRemainder())
        .noneMatch(calculation -> calculation.getOperand1() % calculation.getOperand2() > 10)
        .noneMatch(calculation -> calculation.getResult() > 1_000)
        .allMatch(calculation -> fixedDivisors.contains((int) calculation.getOperand2()))
        .noneMatch(calculation -> exclusions.contains((int) calculation.getOperand1()));
  }
}
