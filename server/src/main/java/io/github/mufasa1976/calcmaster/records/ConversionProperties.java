package io.github.mufasa1976.calcmaster.records;

import io.github.mufasa1976.calcmaster.enums.Unit;
import lombok.Builder;

@Builder
public record ConversionProperties(Unit unit, boolean withKilometers) {
  public Unit getUnit() {
    if (unit == Unit.METER && withKilometers) {
      return Unit.KILOMETER;
    }
    return unit;
  }
}
