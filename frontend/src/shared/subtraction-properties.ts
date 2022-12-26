import { UNLIMITED_TRANSACTIONS } from "./calculation-properties";

export interface SubtractionProperties {
  minDifference: number;
  maxDifference: number;
  subtrahendRounding: number;
  includeZeroOnOperand: boolean;
  transgression: number;
}

export const initialSubtractionProperties = {
  minDifference: 0,
  maxDifference: 10,
  subtrahendRounding: 1,
  includeZeroOnOperand: true,
  transgression: UNLIMITED_TRANSACTIONS
} as SubtractionProperties;
