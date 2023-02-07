package io.github.mufasa1976.calcmaster.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Unit {
  GRAM("g", new UnitPrefix[] {
      UnitPrefix.BASE,
      UnitPrefix.DEKA,
      UnitPrefix.KILO
  }, new UnitConversion[] {
      UnitConversion.WHOLE_NUMBERS,
      UnitConversion.MATRIX,
      UnitConversion.UPSCALE
  }),
  LITRE("l", new UnitPrefix[] {
      UnitPrefix.BASE,
      UnitPrefix.DECI,
      UnitPrefix.CENTI,
      UnitPrefix.MILLI
  }, new UnitConversion[] {
      UnitConversion.WHOLE_NUMBERS
  }),
  METER("m", new UnitPrefix[] {
      UnitPrefix.BASE,
      UnitPrefix.DECI,
      UnitPrefix.CENTI,
      UnitPrefix.MILLI
  }, new UnitConversion[] {
      UnitConversion.WHOLE_NUMBERS,
      UnitConversion.MATRIX,
      UnitConversion.UPSCALE
  }),
  KILOMETER("m", new UnitPrefix[] {
      UnitPrefix.BASE,
      UnitPrefix.MILLI,
      UnitPrefix.KILO
  }, new UnitConversion[] {
      UnitConversion.WHOLE_NUMBERS
  }),
  SECOND("s", new UnitPrefix[] {
      UnitPrefix.BASE,
      UnitPrefix.MILLI,
      UnitPrefix.MICRO,
      UnitPrefix.NANO
  }, new UnitConversion[] {
      UnitConversion.WHOLE_NUMBERS
  });

  private final String unitSymbol;
  private final UnitPrefix[] allowedPrefixes;
  private final UnitConversion[] allowedUnitConversions;
}
