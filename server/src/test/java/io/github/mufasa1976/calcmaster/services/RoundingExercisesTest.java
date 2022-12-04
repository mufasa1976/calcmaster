package io.github.mufasa1976.calcmaster.services;

import io.github.mufasa1976.calcmaster.dtos.Calculation;
import io.github.mufasa1976.calcmaster.enums.Operator;
import io.github.mufasa1976.calcmaster.records.CalculationProperties;
import io.github.mufasa1976.calcmaster.records.Calculations;
import io.github.mufasa1976.calcmaster.records.RoundingProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"spring.main.banner-mode=off", "application.max-number-of-calculations=1000000"})
public class RoundingExercisesTest {
  private static final int NUMBER_OF_EXERCISES = 1_000_000;

  @Autowired
  private CalculationService calculationService;

  @Test
  @DisplayName("Rounding Exercises with initial Parameters")
  void roundingExercisesWithInitialValues() {
    // GIVEN
    final var calculationProperties =
        CalculationProperties.builder()
                             .operators(List.of(Operator.ROUND))
                             .numberOfCalculations(NUMBER_OF_EXERCISES)
                             .roundingProperties(
                                 RoundingProperties.builder()
                                                   .minPower(1)
                                                   .maxPower(2)
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
        .allMatch(calculation -> (Math.round(Long.valueOf(calculation.getOperand1()).doubleValue() / calculation.getOperand2()) * calculation.getOperand2()) == calculation.getResult())
        .noneMatch(calculation -> calculation.getOperand1() < 10)   // 10¹
        .noneMatch(calculation -> calculation.getOperand2() < 10)   // 10¹
        .noneMatch(calculation -> calculation.getOperand1() > 100)  // 10²
        .noneMatch(calculation -> calculation.getOperand2() > 100); // 10²
  }

  @Test
  @DisplayName("Rounding Exercises with minPower = 3 and maxPower = 9")
  void roundingExercisesWithMinPower3AndMaxPower10() {
    // GIVEN
    final var calculationProperties =
        CalculationProperties.builder()
                             .operators(List.of(Operator.ROUND))
                             .numberOfCalculations(NUMBER_OF_EXERCISES)
                             .roundingProperties(
                                 RoundingProperties.builder()
                                                   .minPower(3)
                                                   .maxPower(9)
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
        .allMatch(calculation -> (Math.round(Long.valueOf(calculation.getOperand1()).doubleValue() / calculation.getOperand2()) * calculation.getOperand2()) == calculation.getResult())
        .noneMatch(calculation -> calculation.getOperand1() < 1_000)   // 10³
        .noneMatch(calculation -> calculation.getOperand2() < 1_000)   // 10³
        .noneMatch(calculation -> calculation.getOperand1() > 1_000_000_000)  // 10⁹
        .noneMatch(calculation -> calculation.getOperand2() > 1_000_000_000); // 10⁹
  }
}
