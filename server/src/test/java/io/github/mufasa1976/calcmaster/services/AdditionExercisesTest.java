package io.github.mufasa1976.calcmaster.services;

import io.github.mufasa1976.calcmaster.dtos.Calculation;
import io.github.mufasa1976.calcmaster.enums.Operator;
import io.github.mufasa1976.calcmaster.records.AdditionProperties;
import io.github.mufasa1976.calcmaster.records.CalculationProperties;
import io.github.mufasa1976.calcmaster.records.Calculations;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"spring.main.banner-mode=off", "application.max-number-of-calculations=1000000"})
class AdditionExercisesTest {
  private static final int NUMBER_OF_EXERCISES = 1_000_000;

  @Autowired
  private CalculationService calculationService;

  @Test
  @DisplayName("Addition Exercises with initial Parameters")
  void additionExercisesWithInitialValues() {
    // GIVEN
    final var calculationProperties =
        CalculationProperties.builder()
                             .operators(List.of(Operator.ADD))
                             .numberOfCalculations(NUMBER_OF_EXERCISES)
                             .additionProperties(
                                 AdditionProperties.builder()
                                                   .minSum(0)
                                                   .maxSum(10)
                                                   .secondAddendRounding(1)
                                                   .includeZeroOnOperand(true)
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
        .allMatch(calculation -> calculation.getOperand1() + calculation.getOperand2() == calculation.getResult())
        .noneMatch(calculation -> calculation.getResult() > 10);
  }

  @Test
  @DisplayName("Addition Exercises with minSum = 10 and maxSum = 100 and secondAddendRounding = 10 without Zero on Operands and vertical Layout")
  void additionExercisesWithMinSum10AndMaxSum100AndSecondAddendRounding10AndNoZeroAsOperandAndVerticalLayout() {
    // GIVEN
    final var calculationProperties =
        CalculationProperties.builder()
                             .operators(List.of(Operator.ADD))
                             .numberOfCalculations(NUMBER_OF_EXERCISES)
                             .verticalDisplay(true)
                             .additionProperties(
                                 AdditionProperties.builder()
                                                   .minSum(10)
                                                   .maxSum(100)
                                                   .secondAddendRounding(10)
                                                   .includeZeroOnOperand(false)
                                                   .build())
                             .build();

    // WHEN
    final var calculationsCandidate = calculationService.createCalculations(calculationProperties, Locale.ENGLISH);

    // THEN
    assertThat(calculationsCandidate).isNotNull();
    final var calculations = calculationsCandidate.blockOptional().orElseThrow();
    assertThat(calculations).extracting(Calculations::subheader, Calculations::verticalDisplay).contains(null, true);
    assertThat(calculations.calculations())
        .hasSize(NUMBER_OF_EXERCISES)
        .allMatch(calculation -> calculation.getType() == Calculation.Type.CALCULATION)
        .allMatch(calculation -> calculation.getOperand1() + calculation.getOperand2() == calculation.getResult())
        .noneMatch(calculation -> calculation.getResult() < 10)
        .noneMatch(calculation -> calculation.getResult() > 100)
        .allMatch(calculation -> calculation.getOperand2() % 10 == 0)
        .noneMatch(calculation -> calculation.getOperand1() == 0)
        .noneMatch(calculation -> calculation.getOperand2() == 0);
  }

  @Test
  @DisplayName("Addition Exercises with maxSum = 1,000,000 and secondAddendRounding = 100")
  void additionExercisesWithMaxSum1000000AndSecondAddendRounding100() {
    // GIVEN
    final var calculationProperties =
        CalculationProperties.builder()
                             .operators(List.of(Operator.ADD))
                             .numberOfCalculations(NUMBER_OF_EXERCISES)
                             .additionProperties(
                                 AdditionProperties.builder()
                                                   .minSum(0)
                                                   .maxSum(1_000_000)
                                                   .secondAddendRounding(100)
                                                   .includeZeroOnOperand(true)
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
        .allMatch(calculation -> calculation.getOperand1() + calculation.getOperand2() == calculation.getResult())
        .noneMatch(calculation -> calculation.getResult() > 1_000_000)
        .allMatch(calculation -> calculation.getOperand2() % 100 == 0);
  }
}
