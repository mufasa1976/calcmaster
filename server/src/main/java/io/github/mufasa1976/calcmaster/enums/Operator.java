package io.github.mufasa1976.calcmaster.enums;

import lombok.RequiredArgsConstructor;
import io.github.mufasa1976.calcmaster.dtos.Calculation;
import io.github.mufasa1976.calcmaster.suppliers.AdditionSupplier;
import io.github.mufasa1976.calcmaster.suppliers.DivisionSupplier;
import io.github.mufasa1976.calcmaster.suppliers.MultiplicationSupplier;
import io.github.mufasa1976.calcmaster.suppliers.SubtractionSupplier;

import java.util.function.Supplier;

@RequiredArgsConstructor
public enum Operator {
  ADD(AdditionSupplier.class),
  SUBTRACT(SubtractionSupplier.class),
  MULTIPLY(MultiplicationSupplier.class),
  DIVIDE(DivisionSupplier.class);

  private final Class<? extends Supplier<Calculation>> calculationSupplier;
}
