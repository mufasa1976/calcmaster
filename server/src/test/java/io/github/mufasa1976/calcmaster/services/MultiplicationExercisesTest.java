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

import static io.github.mufasa1976.calcmaster.enums.MultiplicationType.HALF_OF_PRODUCT;
import static io.github.mufasa1976.calcmaster.enums.MultiplicationType.SQRT;
import static io.github.mufasa1976.calcmaster.records.CalculationProperties.UNLIMITED_TRANSGRESSIONS;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"spring.main.banner-mode=off", "application.max-number-of-calculations=1000000"})
public class MultiplicationExercisesTest implements DigitTest {
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
                                                         .type(SQRT)
                                                         .maxProduct(100)
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
        .allMatch(calculation -> calculation.getOperand1() * calculation.getOperand2() == calculation.getResult())
        .noneMatch(calculation -> calculation.getResult() > 100)
        .noneMatch(calculation -> calculation.getOperand1() > 10)
        .noneMatch(calculation -> calculation.getOperand2() > 10);
  }

  @Test
  @DisplayName("Multiplication Exercises with Type SQRT and maxProduct = 1,000 and exclusions = [0]")
  void multiplicationExercisesWithTypeSQRTAndMaxProduct1000AndWithoutZero() {
    // GIVEN
    final var exclusions = List.of(0);
    final var calculationProperties =
        CalculationProperties.builder()
                             .operators(List.of(Operator.MULTIPLY))
                             .numberOfCalculations(NUMBER_OF_EXERCISES)
                             .multiplicationProperties(
                                 MultiplicationProperties.builder()
                                                         .type(SQRT)
                                                         .maxProduct(1_000)
                                                         .exclusions(exclusions)
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
        .allMatch(calculation -> calculation.getOperand1() * calculation.getOperand2() == calculation.getResult())
        .noneMatch(calculation -> calculation.getResult() > 1_000)
        .noneMatch(calculation -> calculation.getOperand1() > 32) // round(sqrt(1,000))
        .noneMatch(calculation -> calculation.getOperand2() > 31) // round(sqrt(1,000)) - 1
        .noneMatch(calculation -> exclusions.contains((int) calculation.getOperand1()));
  }

  @Test
  @DisplayName("Multiplication Exercises with Type SQRT and maxProduct = 1,000 and exclusions = [0, 2] and fixedMultiplicands = [3, 6]")
  void multiplicationExercisesWithTypeSQRTAndMaxProduct1000AndWithoutZeroAnd2AndWithFixedMultiplicandsTo3And6() {
    // GIVEN
    final var exclusions = List.of(0, 2);
    final var fixedMultiplicands = List.of(3, 6);
    final int maxMultiplicand = fixedMultiplicands.stream().max(Integer::compareTo).orElse(1);
    final var maxMultiplier = 1_000 / maxMultiplicand;
    final var calculationProperties =
        CalculationProperties.builder()
                             .operators(List.of(Operator.MULTIPLY))
                             .numberOfCalculations(NUMBER_OF_EXERCISES)
                             .multiplicationProperties(
                                 MultiplicationProperties.builder()
                                                         .type(SQRT)
                                                         .maxProduct(1_000)
                                                         .exclusions(exclusions)
                                                         .fixedMultiplicands(fixedMultiplicands)
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
        .allMatch(calculation -> calculation.getOperand1() * calculation.getOperand2() == calculation.getResult())
        .noneMatch(calculation -> calculation.getResult() > 1_000)
        .noneMatch(calculation -> calculation.getOperand1() > maxMultiplier)
        .allMatch(calculation -> fixedMultiplicands.contains((int) calculation.getOperand2()))
        .noneMatch(calculation -> exclusions.contains((int) calculation.getOperand1()));
  }

  @Test
  @DisplayName("Multiplication Exercises with Type HALF_OF_PRODUCT and maxProduct = 1,000 and exclusions = [0]")
  void multiplicationExercisesWithTypeHALF_OF_PRODUCTAndMaxProduct1000AndWithoutZero() {
    // GIVEN
    final var exclusions = List.of(0);
    final var calculationProperties =
        CalculationProperties.builder()
                             .operators(List.of(Operator.MULTIPLY))
                             .numberOfCalculations(NUMBER_OF_EXERCISES)
                             .multiplicationProperties(
                                 MultiplicationProperties.builder()
                                                         .type(HALF_OF_PRODUCT)
                                                         .maxProduct(1_000)
                                                         .exclusions(exclusions)
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
        .allMatch(calculation -> calculation.getOperand1() * calculation.getOperand2() == calculation.getResult())
        .noneMatch(calculation -> calculation.getResult() > 1_000)
        .noneMatch(calculation -> calculation.getOperand1() > 500)
        .noneMatch(calculation -> exclusions.contains((int) calculation.getOperand1()));
  }

  @Test
  @DisplayName("Multiplication Exercises with Type HALF_OF_PRODUCT and maxProduct = 1,000 and exclusions = [0, 2] and fixedMultiplicands = [3, 6]")
  void multiplicationExercisesWithTypeHALF_OF_PRODUCTAndMaxProduct1000AndWithoutZeroAnd2AndWithFixedMultiplicandsTo3And6() {
    // GIVEN
    final var exclusions = List.of(0, 2);
    final var fixedMultiplicands = List.of(3, 6);
    final int maxMultiplicand = fixedMultiplicands.stream().max(Integer::compareTo).orElse(1);
    final var maxMultiplier = 1_000 / maxMultiplicand;
    final var calculationProperties =
        CalculationProperties.builder()
                             .operators(List.of(Operator.MULTIPLY))
                             .numberOfCalculations(NUMBER_OF_EXERCISES)
                             .multiplicationProperties(
                                 MultiplicationProperties.builder()
                                                         .type(HALF_OF_PRODUCT)
                                                         .maxProduct(1_000)
                                                         .exclusions(exclusions)
                                                         .fixedMultiplicands(fixedMultiplicands)
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
        .allMatch(calculation -> calculation.getOperand1() * calculation.getOperand2() == calculation.getResult())
        .noneMatch(calculation -> calculation.getResult() > 1_000)
        .noneMatch(calculation -> calculation.getOperand1() > maxMultiplier)
        .allMatch(calculation -> fixedMultiplicands.contains((int) calculation.getOperand2()))
        .noneMatch(calculation -> exclusions.contains((int) calculation.getOperand1()));
  }

  @Test
  @DisplayName("Multiplication Exercises with maxProduct = 100,000 and Transgression = 25 [11001] (2nd and 3rd should have no Transgression)")
  void multiplicationExercisesWithMaxProduct100000AndWithTransgression25() {
    // GIVEN
    final var calculationProperties =
        CalculationProperties.builder()
                             .operators(List.of(Operator.MULTIPLY))
                             .numberOfCalculations(NUMBER_OF_EXERCISES)
                             .multiplicationProperties(
                                 MultiplicationProperties.builder()
                                                         .maxProduct(100_000)
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
        .allMatch(calculation -> calculation.getOperand1() * calculation.getOperand2() == calculation.getResult())
        .noneMatch(calculation -> calculation.getResult() > 100_000)
        .noneMatch(calculation -> calculation.getOperand2() < 2)
        .noneMatch(calculation -> calculation.getOperand2() > 9)
        .anyMatch(calculation -> getDigit(calculation.getOperand1(), 1) * calculation.getOperand2() > 10)
        .noneMatch(calculation -> getDigit(calculation.getOperand1(), 2) * calculation.getOperand2() > 10)
        .noneMatch(calculation -> getDigit(calculation.getOperand1(), 3) * calculation.getOperand2() > 10)
        .anyMatch(calculation -> getDigit(calculation.getOperand1(), 4) * calculation.getOperand2() > 10)
        .noneMatch(calculation -> getDigit(calculation.getOperand1(), 5) * calculation.getOperand2() > 10);
  }
}
