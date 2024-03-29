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

import static io.github.mufasa1976.calcmaster.records.CalculationProperties.UNLIMITED_TRANSGRESSIONS;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"spring.main.banner-mode=off", "application.max-number-of-calculations=1000000"})
class AdditionExercisesTest implements DigitTest {
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
                                                   .maxSum(10)
                                                   .secondAddendRounding(1)
                                                   .transgression(UNLIMITED_TRANSGRESSIONS)
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
        .allMatch(calculation -> calculation.getOperand1() + calculation.getOperand2() == calculation.getResult())
        .noneMatch(calculation -> calculation.getResult() > 10);
  }

  @Test
  @DisplayName("Addition Exercises with maxSum = 100 and minOperand = 1 and secondAddendRounding = 10 and vertical Layout")
  void additionExercisesWithMaxSum100AndMinOperand1AndSecondAddendRounding10AndVerticalLayout() {
    // GIVEN
    final var calculationProperties =
        CalculationProperties.builder()
                             .operators(List.of(Operator.ADD))
                             .numberOfCalculations(NUMBER_OF_EXERCISES)
                             .verticalDisplay(true)
                             .additionProperties(
                                 AdditionProperties.builder()
                                                   .maxSum(100)
                                                   .minOperand(1)
                                                   .secondAddendRounding(10)
                                                   .transgression(UNLIMITED_TRANSGRESSIONS)
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
        .allMatch(calculation -> calculation.getOperand1() + calculation.getOperand2() == calculation.getResult())
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
                                                   .maxSum(1_000_000)
                                                   .secondAddendRounding(100)
                                                   .transgression(UNLIMITED_TRANSGRESSIONS)
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
        .allMatch(calculation -> calculation.getOperand1() + calculation.getOperand2() == calculation.getResult())
        .noneMatch(calculation -> calculation.getResult() > 1_000_000)
        .allMatch(calculation -> calculation.getOperand2() % 100 == 0);
  }

  @Test
  @DisplayName("Addition Exercises with maxSum = 100,000 and transgression = 25 [11001] (2nd and 3rd Position should have no Transgression)")
  void additionExercisesWithMaxSum100000AndTransgression25() {
    // GIVEN
    final var calculationProperties =
        CalculationProperties.builder()
                             .operators(List.of(Operator.ADD))
                             .numberOfCalculations(NUMBER_OF_EXERCISES)
                             .additionProperties(
                                 AdditionProperties.builder()
                                                   .maxSum(100_000)
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
        .allMatch(calculation -> calculation.getOperand1() + calculation.getOperand2() == calculation.getResult())
        .noneMatch(calculation -> calculation.getResult() > 100_000)
        .anyMatch(calculation -> getDigit(calculation.getOperand1(), 1) + getDigit(calculation.getOperand2(), 1) > 10)
        .noneMatch(calculation -> getDigit(calculation.getOperand1(), 2) + getDigit(calculation.getOperand2(), 2) > 10)
        .noneMatch(calculation -> getDigit(calculation.getOperand1(), 3) + getDigit(calculation.getOperand2(), 3) > 10)
        .anyMatch(calculation -> getDigit(calculation.getOperand1(), 4) + getDigit(calculation.getOperand2(), 4) > 10)
        .noneMatch(calculation -> getDigit(calculation.getOperand1(), 5) + getDigit(calculation.getOperand2(), 5) > 10);
  }

  @Test
  @DisplayName("Addition Exercises with maxSum = 20 and transgression = 2 [10] (1st Position should have no Transgression)")
  void additionExercisesWithMaxSum20AndTransgression0() {
    // GIVEN
    final var calculationProperties =
        CalculationProperties.builder()
                             .operators(List.of(Operator.ADD))
                             .numberOfCalculations(20)
                             .additionProperties(
                                 AdditionProperties.builder()
                                                   .maxSum(20)
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
        .allMatch(calculation -> calculation.getOperand1() + calculation.getOperand2() == calculation.getResult())
        .noneMatch(calculation -> calculation.getResult() > 20)
        .noneMatch(calculation -> getDigit(calculation.getOperand1(), 1) + getDigit(calculation.getOperand2(), 1) > 10);
  }

  @Test
  @DisplayName("Addition Exercises with maxSum = 100 and minOperand = 1 and secondAddendRounding = 0")
  void additionExcercisesWithMaxSum100AndMinOperand1AndSecondAddendRounding0() {
    // GIVEN
    final var calculationProperties =
        CalculationProperties.builder()
                             .operators(List.of(Operator.ADD))
                             .numberOfCalculations(NUMBER_OF_EXERCISES)
                             .additionProperties(
                                 AdditionProperties.builder()
                                                   .maxSum(100)
                                                   .minOperand(1)
                                                   .secondAddendRounding(0)
                                                   .transgression(UNLIMITED_TRANSGRESSIONS)
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
        .allMatch(calculation -> calculation.getOperand1() + calculation.getOperand2() == calculation.getResult())
        .noneMatch(calculation -> calculation.getResult() > 100)
        .noneMatch(calculation -> calculation.getOperand2() >= 10)
        .noneMatch(calculation -> calculation.getOperand2() == 0);
  }

  @Test
  @DisplayName("Addition Exercises with maxSum = 30 and minOperand = 10")
  void additionExercisesWithMaxSum30AndMinOperand10() {
    // GIVEN
    final var calculationProperties =
        CalculationProperties.builder()
                             .operators(List.of(Operator.ADD))
                             .numberOfCalculations(NUMBER_OF_EXERCISES)
                             .additionProperties(
                                 AdditionProperties.builder()
                                                   .maxSum(30)
                                                   .minOperand(10)
                                                   .secondAddendRounding(1)
                                                   .transgression(UNLIMITED_TRANSGRESSIONS)
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
        .allMatch(calculation -> calculation.getOperand1() + calculation.getOperand2() == calculation.getResult())
        .noneMatch(calculation -> calculation.getResult() > 30)
        .noneMatch(calculation -> calculation.getOperand1() < 10)
        .noneMatch(calculation -> calculation.getOperand2() < 10);
  }

  @Test
  @DisplayName("Addition Exercises with maxSum = 1,000,000 and minOperand = 100,000")
  void additionExercisesWithMaxSum1000000AndMinOperand100000() {
    // GIVEN
    final var calculationProperties =
        CalculationProperties.builder()
                             .operators(List.of(Operator.ADD))
                             .numberOfCalculations(NUMBER_OF_EXERCISES)
                             .additionProperties(
                                 AdditionProperties.builder()
                                                   .maxSum(1_000_000)
                                                   .minOperand(100_000)
                                                   .secondAddendRounding(1)
                                                   .transgression(UNLIMITED_TRANSGRESSIONS)
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
        .allMatch(calculation -> calculation.getOperand1() + calculation.getOperand2() == calculation.getResult())
        .noneMatch(calculation -> calculation.getResult() > 1_000_000)
        .noneMatch(calculation -> calculation.getOperand1() < 100_000)
        .noneMatch(calculation -> calculation.getOperand2() < 100_000);
  }
}
