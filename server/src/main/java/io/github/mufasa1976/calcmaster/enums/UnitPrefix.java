package io.github.mufasa1976.calcmaster.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

@RequiredArgsConstructor
@Getter
public enum UnitPrefix {
  BASE(1, StringUtils.EMPTY),
  DEKA(10.0, "da"),
  HEKTO(100.0, "h"),
  KILO(1_000.0, "k"),
  MEGA(1_000_000.0, "M"),
  GIGA(1_000_000_000.0, "G"),
  TERA(1_000_000_000_000.0, "T"),
  PETA(1_000_000_000_000_000.0, "P"),
  DECI(0.1, "d"),
  CENTI(0.01, "c"),
  MILLI(0.001, "m"),
  MICRO(0.000_001, "μ"),
  NANO(0.000_000_001, "n"),
  PICO(0.000_000_000_001, "p");

  private final double factor;
  private final String prefixSymbol;

  public static Optional<UnitPrefix> ofValue(String value, Unit unit) {
    if (StringUtils.isBlank(value)) {
      return Optional.empty();
    }

    final var prefixSymbol = StringUtils.removeEnd(value, unit.getUnitSymbol());
    if (StringUtils.isBlank(prefixSymbol)) {
      return Optional.empty();
    }

    for (UnitPrefix unitPrefix : values()) {
      if (prefixSymbol.equals(unitPrefix.prefixSymbol)) {
        return Optional.of(unitPrefix);
      }
    }
    return Optional.empty();
  }
}
