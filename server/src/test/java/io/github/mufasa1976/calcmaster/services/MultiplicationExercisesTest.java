package io.github.mufasa1976.calcmaster.services;

import io.github.mufasa1976.calcmaster.dtos.Calculation;
import io.github.mufasa1976.calcmaster.enums.Operator;
import io.github.mufasa1976.calcmaster.records.CalculationProperties;
import io.github.mufasa1976.calcmaster.records.Calculations;
import io.github.mufasa1976.calcmaster.records.MultiplicationProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"spring.main.banner-mode=off", "application.max-number-of-calculations=1000000"})
public class MultiplicationExercisesTest {
  private static final int NUMBER_OF_EXERCISES = 1_000_000;

  @Autowired
  private CalculationService calculationService;

  @Test
  @DisplayName("Multiplication Exercises with initial Parameters")
  void multiplicationExercisesWithInitialValues() {
    // GIVEN
    final var calculationProperties =
        CalculationProperties.builder()
                             .operators(List.of(Operator.MULTIPLY))
                             .numberOfCalculations(NUMBER_OF_EXERCISES)
                             .multiplicationProperties(
                                 MultiplicationProperties.builder()
                                                         .maxProduct(100)
                                                         .build())
                             .build();

    // WHEN
    final var calculationsCandidate = calculationService.createCalculations(calculationProperties, Locale.ENGLISH);

    // THEN
    assertThat(calculationsCandidate).isPresent();
    final var calculations = calculationsCandidate.orElseThrow();
    assertThat(calculations).extracting(Calculations::subheader, Calculations::verticalDisplay).contains(null, false);
    assertThat(calculations.calculations())
        .hasSize(NUMBER_OF_EXERCISES)
        .allMatch(calculation -> calculation.getType() == Calculation.Type.CALCULATION)
        .allMatch(calculation -> calculation.getOperand1() * calculation.getOperand2() == calculation.getResult())
        .noneMatch(calculation -> calculation.getResult() > 100);
  }

  @Test
  @DisplayName("Multiplication Exercises with maxProduct = 1,000 and exclusions = [0, 2] and fixedMultiplicands = [3, 6]")
  void multiplicationExercisesWithMaxProduct1000AndWithoutZeroAnd2AndWithFixedMultiplicandsTo3And6() {
    // GIVEN
    final var exclusions = List.of(0, 2);
    final var fixedMultiplicands = List.of(3, 6);
    final var calculationProperties =
        CalculationProperties.builder()
                             .operators(List.of(Operator.MULTIPLY))
                             .numberOfCalculations(NUMBER_OF_EXERCISES)
                             .multiplicationProperties(
                                 MultiplicationProperties.builder()
                                                         .maxProduct(1_000)
                                                         .exclusions(exclusions)
                                                         .fixedMultiplicands(fixedMultiplicands)
                                                         .build())
                             .build();

    // WHEN
    final var calculationsCandidate = calculationService.createCalculations(calculationProperties, Locale.ENGLISH);

    // THEN
    assertThat(calculationsCandidate).isPresent();
    final var calculations = calculationsCandidate.orElseThrow();
    assertThat(calculations).extracting(Calculations::subheader, Calculations::verticalDisplay).contains(null, false);
    assertThat(calculations.calculations())
        .hasSize(NUMBER_OF_EXERCISES)
        .allMatch(calculation -> calculation.getType() == Calculation.Type.CALCULATION)
        .allMatch(calculation -> calculation.getOperand1() * calculation.getOperand2() == calculation.getResult())
        .noneMatch(calculation -> calculation.getResult() > 1_000)
        .allMatch(calculation -> fixedMultiplicands.contains((int) calculation.getOperand2()))
        .noneMatch(calculation -> exclusions.contains((int) calculation.getOperand1()));
  }
}
