import { AdditionProperties, initialAdditionProperties } from "./additionProperties";
import { initialSubtractionProperties, SubtractionProperties } from "./subtractionProperties";
import { initialMultiplicationProperties, MultiplicationProperties } from "./multiplicationProperties";
import { DivisionProperties, initialDivisionProperties } from "./divisionProperties";
import { initialRoundingProperties, RoundingProperties } from "./roundingProperties";

export interface CalculationProperties {
  operators: string[];
  additionProperties: AdditionProperties;
  subtractionProperties: SubtractionProperties;
  multiplicationProperties: MultiplicationProperties;
  divisionProperties: DivisionProperties;
  roundingProperties: RoundingProperties;
  subheader?: string;
  toggleHide: boolean;
  numberOfCalculations: number;
  verticalDisplay: boolean;
}

export const initialCalculationProperties = {
  operators: [],
  additionProperties: initialAdditionProperties,
  subtractionProperties: initialSubtractionProperties,
  multiplicationProperties: initialMultiplicationProperties,
  divisionProperties: initialDivisionProperties,
  roundingProperties: initialRoundingProperties,
  toggleHide: true,
  numberOfCalculations: 15,
  verticalDisplay: false
} as CalculationProperties;
