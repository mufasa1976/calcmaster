package io.github.mufasa1976.calcmaster.services;

import io.github.mufasa1976.calcmaster.dtos.Calculation;
import io.github.mufasa1976.calcmaster.enums.Operator;
import io.github.mufasa1976.calcmaster.records.CalculationProperties;
import io.github.mufasa1976.calcmaster.records.Calculations;
import io.github.mufasa1976.calcmaster.records.SubtractionProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Locale;

import static io.github.mufasa1976.calcmaster.records.CalculationProperties.UNLIMITED_TRANSGRESSIONS;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"spring.main.banner-mode=off", "application.max-number-of-calculations=1000000"})
public class SubtractionExercisesTest implements DigitTest {
  private static final int NUMBER_OF_EXERCISES = 1_000_000;

  @Autowired
  private CalculationService calculationService;

  @Test
  @DisplayName("Subtraction Exercises with initial Parameters")
  void subtractionExercisesWithInitialValues() {
    // GIVEN
    final var calculationProperties =
        CalculationProperties.builder()
                             .operators(List.of(Operator.SUBTRACT))
                             .numberOfCalculations(NUMBER_OF_EXERCISES)
                             .subtractionProperties(
                                 SubtractionProperties.builder()
                                                      .transgression(UNLIMITED_TRANSGRESSIONS)
                                                      .minDifference(0)
                                                      .maxDifference(10)
                                                      .subtrahendRounding(1)
                                                      .includeZeroOnOperand(true)
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
        .allMatch(calculation -> calculation.getOperand1() - calculation.getOperand2() == calculation.getResult())
        .noneMatch(calculation -> calculation.getResult() > 10);
  }

  @Test
  @DisplayName("Subtraction Exercises with minDifference = 10 and maxDifference = 100 and subtrahendRounding = 10 without Zero on Operands with vertical Layout")
  void subtractionExercisesWithMinDifference10AndMaxDifference100AndSubtrahendRounding10AndNoZeroAsOperandAndVerticalLayout() {
    // GIVEN
    final var calculationProperties =
        CalculationProperties.builder()
                             .operators(List.of(Operator.SUBTRACT))
                             .numberOfCalculations(NUMBER_OF_EXERCISES)
                             .verticalDisplay(true)
                             .subtractionProperties(
                                 SubtractionProperties.builder()
                                                      .transgression(UNLIMITED_TRANSGRESSIONS)
                                                      .minDifference(10)
                                                      .maxDifference(100)
                                                      .subtrahendRounding(10)
                                                      .includeZeroOnOperand(false)
                                                      .build())
                             .build();

    // WHEN
    final var calculationsCandidate = calculationService.createCalculations(calculationProperties, Locale.ENGLISH);

    // THEN
    assertThat(calculationsCandidate).isPresent();
    final var calculations = calculationsCandidate.orElseThrow();
    assertThat(calculations).extracting(Calculations::subheader, Calculations::verticalDisplay).contains(null, true);
    assertThat(calculations.calculations())
        .hasSize(NUMBER_OF_EXERCISES)
        .allMatch(calculation -> calculation.getType() == Calculation.Type.CALCULATION)
        .allMatch(calculation -> calculation.getOperand1() - calculation.getOperand2() == calculation.getResult())
        .noneMatch(calculation -> calculation.getResult() < 10)
        .noneMatch(calculation -> calculation.getResult() > 100)
        .allMatch(calculation -> calculation.getOperand2() % 10 == 0)
        .noneMatch(calculation -> calculation.getOperand1() == 0)
        .noneMatch(calculation -> calculation.getOperand2() == 0);
  }

  @Test
  @DisplayName("Subtraction Exercises with maxDifference = 1,000,000 and subtrahendRounding = 100")
  void subtractionExercisesWithMaxDifference1000000AndSubtrahendRounding100() {
    // GIVEN
    final var calculationProperties =
        CalculationProperties.builder()
                             .operators(List.of(Operator.SUBTRACT))
                             .numberOfCalculations(NUMBER_OF_EXERCISES)
                             .subtractionProperties(
                                 SubtractionProperties.builder()
                                                      .transgression(UNLIMITED_TRANSGRESSIONS)
                                                      .maxDifference(1_000_000)
                                                      .subtrahendRounding(100)
                                                      .includeZeroOnOperand(true)
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
        .allMatch(calculation -> calculation.getOperand1() - calculation.getOperand2() == calculation.getResult())
        .noneMatch(calculation -> calculation.getResult() > 1_000_000)
        .allMatch(calculation -> calculation.getOperand2() % 100 == 0);
  }

  @Test
  @DisplayName("Subtraction Exercises with maxDifference = 100,000 and transgression = 25 [11001] (2nd and 3rd Position should have no Transgression)")
  void subtractionExercisesWithMaxSum100000AndTransgression25() {
    // GIVEN
    final var calculationProperties =
        CalculationProperties.builder()
                             .operators(List.of(Operator.SUBTRACT))
                             .numberOfCalculations(NUMBER_OF_EXERCISES)
                             .subtractionProperties(
                                 SubtractionProperties.builder()
                                                      .minDifference(0)
                                                      .maxDifference(100_000)
                                                      .includeZeroOnOperand(false)
                                                      .transgression(25) // 2nd and 3rd digit -> [11001] = 25
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
        .allMatch(calculation -> calculation.getOperand1() - calculation.getOperand2() == calculation.getResult())
        .noneMatch(calculation -> calculation.getOperand1() > 100_000)
        .noneMatch(calculation -> calculation.getResult() > 100_000)
        .noneMatch(calculation -> calculation.getResult() < 0)
        .anyMatch(calculation -> getDigit(calculation.getOperand1(), 1) - getDigit(calculation.getOperand2(), 1) < 0)
        .noneMatch(calculation -> getDigit(calculation.getOperand1(), 2) - getDigit(calculation.getOperand2(), 2) < 0)
        .noneMatch(calculation -> getDigit(calculation.getOperand1(), 3) - getDigit(calculation.getOperand2(), 3) < 0)
        .anyMatch(calculation -> getDigit(calculation.getOperand1(), 4) - getDigit(calculation.getOperand2(), 4) < 0)
        .noneMatch(calculation -> getDigit(calculation.getOperand1(), 5) - getDigit(calculation.getOperand2(), 5) < 0);
  }

  @Test
  @DisplayName("Subtraction Exercises with maxDifference = 20 and transgression = 2 [10] (1st Position should have no Transgression)")
  void subtractionExercisesWithMaxSum20AndTransgression0() {
    // GIVEN
    final var calculationProperties =
        CalculationProperties.builder()
                             .operators(List.of(Operator.SUBTRACT))
                             .numberOfCalculations(20)
                             .subtractionProperties(
                                 SubtractionProperties.builder()
                                                      .minDifference(0)
                                                      .maxDifference(20)
                                                      .includeZeroOnOperand(false)
                                                      .transgression(2) // 1st digit = [10] = 2
                                                      .build())
                             .build();

    // WHEN
    final var calculationsCandidate = calculationService.createCalculations(calculationProperties, Locale.ENGLISH);

    // THEN
    assertThat(calculationsCandidate).isPresent();
    final var calculations = calculationsCandidate.orElseThrow();
    assertThat(calculations).extracting(Calculations::subheader, Calculations::verticalDisplay).contains(null, false);
    assertThat(calculations.calculations())
        .hasSize(20)
        .allMatch(calculation -> calculation.getType() == Calculation.Type.CALCULATION)
        .allMatch(calculation -> calculation.getOperand1() - calculation.getOperand2() == calculation.getResult())
        .noneMatch(calculation -> calculation.getOperand1() > 20)
        .noneMatch(calculation -> calculation.getResult() > 20)
        .noneMatch(calculation -> calculation.getResult() < 0)
        .noneMatch(calculation -> getDigit(calculation.getOperand1(), 1) - getDigit(calculation.getOperand2(), 1) < 0);
  }
}
