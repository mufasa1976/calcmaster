package io.github.mufasa1976.calcmaster.records;

import io.github.mufasa1976.calcmaster.enums.Operator;
import lombok.Builder;

import java.util.List;
import java.util.Optional;

@Builder
public record CalculationProperties(
    List<Operator> operators,
    AdditionProperties additionProperties,
    SubtractionProperties subtractionProperties,
    MultiplicationProperties multiplicationProperties,
    DivisionProperties divisionProperties,
    RoundingProperties roundingProperties,
    ConversionProperties conversionProperties,
    Optional<String> subheader,
    boolean toggleHide,
    int numberOfCalculations,
    boolean verticalDisplay,
    Optional<String> exerciseReference) {
  public static final int UNLIMITED_TRANSGRESSIONS = -1;
}
