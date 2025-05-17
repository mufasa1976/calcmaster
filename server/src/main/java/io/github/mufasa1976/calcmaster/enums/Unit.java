package io.github.mufasa1976.calcmaster.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.UnaryOperator;

import static lombok.AccessLevel.NONE;

@RequiredArgsConstructor
@Getter
public enum Unit {
  GRAM("g", factor -> factor, new UnitPrefix[] {
      UnitPrefix.BASE,
      UnitPrefix.DEKA,
      UnitPrefix.KILO
  }, new UnitConversionRule[] {
      UnitConversionRule.WHOLE_NUMBERS,
      UnitConversionRule.MATRIX,
      UnitConversionRule.UPSCALE
  }),
  LITRE("l", factor -> factor, new UnitPrefix[] {
      UnitPrefix.HEKTO,
      UnitPrefix.BASE,
      UnitPrefix.DECI,
      UnitPrefix.CENTI,
      UnitPrefix.MILLI
  }, new UnitConversionRule[] {
      UnitConversionRule.WHOLE_NUMBERS
  }),
  METER("m", factor -> factor, new UnitPrefix[] {
      UnitPrefix.BASE,
      UnitPrefix.DECI,
      UnitPrefix.CENTI,
      UnitPrefix.MILLI
  }, new UnitConversionRule[] {
      UnitConversionRule.WHOLE_NUMBERS,
      UnitConversionRule.MATRIX,
      UnitConversionRule.UPSCALE
  }),
  SQUARE_METER("m²", factor -> Math.pow(factor, 2.0), new UnitPrefix[] {
      UnitPrefix.BASE,
      UnitPrefix.DECI,
      UnitPrefix.CENTI,
      UnitPrefix.MILLI
  }, new UnitConversionRule[] {
      UnitConversionRule.WHOLE_NUMBERS,
      UnitConversionRule.MATRIX,
      UnitConversionRule.UPSCALE
  }),
  CUBIC_METER("m³", factor -> Math.pow(factor, 3.0), new UnitPrefix[] {
      UnitPrefix.BASE,
      UnitPrefix.DECI,
      UnitPrefix.CENTI,
      UnitPrefix.MILLI
  }, new UnitConversionRule[] {
      UnitConversionRule.WHOLE_NUMBERS,
      UnitConversionRule.MATRIX,
      UnitConversionRule.UPSCALE
  }),
  KILOMETER("m", factor -> factor, new UnitPrefix[] {
      UnitPrefix.BASE,
      UnitPrefix.MILLI,
      UnitPrefix.KILO
  }, new UnitConversionRule[] {
      UnitConversionRule.WHOLE_NUMBERS
  }),
  SECOND("s", factor -> factor, new UnitPrefix[] {
      UnitPrefix.BASE,
      UnitPrefix.MILLI,
      UnitPrefix.MICRO,
      UnitPrefix.NANO
  }, new UnitConversionRule[] {
      UnitConversionRule.WHOLE_NUMBERS
  });

  private final String unitSymbol;
  @Getter(NONE)
  private final UnaryOperator<Double> conversionFactor;
  private final UnitPrefix[] allowedPrefixes;
  private final UnitConversionRule[] allowedUnitConversionRules;

  public double convertFactor(double factor) {
    return conversionFactor.apply(factor);
  }
}
