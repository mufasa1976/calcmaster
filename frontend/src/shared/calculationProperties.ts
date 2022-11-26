import {AdditionProperties} from "./additionProperties";
import {SubtractionProperties} from "./subtractionProperties";
import {MultiplicationProperties} from "./multiplicationProperties";
import {DivisionProperties} from "./divisionProperties";
import { RoundingProperties } from "./roundingProperties";

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
