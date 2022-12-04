package io.github.mufasa1976.calcmaster.enums;

import io.github.mufasa1976.calcmaster.dtos.Calculation;
import io.github.mufasa1976.calcmaster.suppliers.*;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public enum Operator {
  ADD(AdditionSupplier.class),
  SUBTRACT(SubtractionSupplier.class),
  MULTIPLY(MultiplicationSupplier.class),
  DIVIDE(DivisionSupplier.class),
  ROUND(RoundingSupplier.class),
  CONVERT(ConversionSupplier.class);

  private final Class<? extends Supplier<Calculation>> calculationSupplier;
}
