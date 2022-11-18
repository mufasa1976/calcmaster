import {AdditionProperties} from "./additionProperties";
import {SubtractionProperties} from "./subtractionProperties";
import {MultiplicationProperties} from "./multiplicationProperties";
import {DivisionProperties} from "./divisionProperties";

export interface CalculationProperties {
  operators: string[];
  additionProperties: AdditionProperties;
  subtractionProperties: SubtractionProperties;
  multiplicationProperties: MultiplicationProperties;
  divisionProperties: DivisionProperties;
  subheader?: string;
  toggleHide: boolean;
  numberOfCalculations: number;
  verticalDisplay: boolean;
}
