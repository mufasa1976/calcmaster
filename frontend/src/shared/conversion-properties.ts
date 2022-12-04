export enum Unit {
  GRAM = "GRAM",
  LITRE = "LITRE",
  METER = "METER",
  SECOND = "SECOND"
}

export interface ConversionProperties {
  unit: Unit;
}

export const initialConversionProperties = {
  unit: Unit.METER
} as ConversionProperties;
