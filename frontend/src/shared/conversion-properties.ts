export enum Unit {
  GRAM = "GRAM",
  LITRE = "LITRE",
  METER = "METER",
  SQUARE_METER = "SQUARE_METER",
  CUBIC_METER = "CUBIC_METER",
  SECOND = "SECOND"
}

export interface ConversionProperties {
  unit: Unit;
  withKilometers: boolean;
}

export const initialConversionProperties = {
  unit: Unit.METER,
  withKilometers: false
} as ConversionProperties;
