import { AdditionProperties, initialAdditionProperties } from "./addition-properties";
import { initialSubtractionProperties, SubtractionProperties } from "./subtraction-properties";
import { initialMultiplicationProperties, MultiplicationProperties } from "./multiplication-properties";
import { DivisionProperties, initialDivisionProperties } from "./division-properties";
import { initialRoundingProperties, RoundingProperties } from "./rounding-properties";
import { ConversionProperties, initialConversionProperties } from "./conversion-properties";

export interface CalculationProperties {
  operators: string[];
  additionProperties: AdditionProperties;
  subtractionProperties: SubtractionProperties;
  multiplicationProperties: MultiplicationProperties;
  divisionProperties: DivisionProperties;
  roundingProperties: RoundingProperties;
  conversionProperties: ConversionProperties;
  subheader?: string;
  toggleHide: boolean;
  numberOfCalculations: number;
  verticalDisplay: boolean;
  exerciseReference?: string;
}

export const initialCalculationProperties = {
  operators: [],
  additionProperties: initialAdditionProperties,
  subtractionProperties: initialSubtractionProperties,
  multiplicationProperties: initialMultiplicationProperties,
  divisionProperties: initialDivisionProperties,
  roundingProperties: initialRoundingProperties,
  conversionProperties: initialConversionProperties,
  toggleHide: true,
  numberOfCalculations: 15,
  verticalDisplay: false
} as CalculationProperties;
