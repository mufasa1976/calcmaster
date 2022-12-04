export enum Unit {
  GRAM = "GRAM",
  LITRE = "LITRE",
  METER = "METER",
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
