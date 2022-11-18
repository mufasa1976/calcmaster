package io.github.mufasa1976.calcmaster.services;

import io.github.mufasa1976.calcmaster.ApplicationProperties;
import io.github.mufasa1976.calcmaster.dtos.Calculation;
import io.github.mufasa1976.calcmaster.records.CalculationProperties;
import io.github.mufasa1976.calcmaster.records.Calculations;
import io.github.mufasa1976.calcmaster.suppliers.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class Calculator {
  private final CalculationProperties calculationProperties;
  private final ApplicationProperties applicationProperties;
  private final Random random;
  private final List<CalculationSupplier> calculationSuppliers;

  public Calculator(CalculationProperties calculationProperties, ApplicationProperties applicationProperties) {
    this.calculationProperties = calculationProperties;
    this.applicationProperties = applicationProperties;

    this.random = new Random();
    this.calculationSuppliers =
        calculationProperties.operators()
                             .stream()
                             .map(operator -> switch (operator) {
                               case ADD ->
                                   new AdditionSupplier(random, calculationProperties.toggleHide(), applicationProperties.getMaxTriesToGetDistinctOperationTuples(),
                                       calculationProperties.additionProperties(), applicationProperties.getMaxTriesToFindSumOfAdditionNotEqualToSecondAddend());
                               case SUBTRACT -> new SubtractionSupplier(random, calculationProperties.toggleHide(),
                                   applicationProperties.getMaxTriesToGetDistinctOperationTuples(), calculationProperties.subtractionProperties());
                               case MULTIPLY -> new MultiplicationSupplier(random, calculationProperties.toggleHide(),
                                   applicationProperties.getMaxTriesToGetDistinctOperationTuples(), calculationProperties.multiplicationProperties());
                               case DIVIDE -> new DivisionSupplier(random, calculationProperties.toggleHide(),
                                   applicationProperties.getMaxTriesToGetDistinctOperationTuples(), calculationProperties.divisionProperties());
                             })
                             .peek(CalculationSupplier::init)
                             .collect(Collectors.toList());
  }

  public Optional<Calculations> calculate() {
    final var calculations = IntStream.range(0, Math.min(calculationProperties.numberOfCalculations(), applicationProperties.getMaxNumberOfCalculations()))
                                      .mapToObj(this::getRandomCalculationSupplier)
                                      .map(Supplier::get)
                                      .toList();
    return Optional.of(
        Calculations.builder()
                    .subheader(calculationProperties.subheader())
                    .calculations(calculations)
                    .verticalDisplay(calculationProperties.verticalDisplay())
                    .build());
  }

  private Supplier<Calculation> getRandomCalculationSupplier(int counter) {
    return calculationSuppliers.get(random.nextInt(calculationSuppliers.size()));
  }
}
