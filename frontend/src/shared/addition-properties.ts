import { UNLIMITED_TRANSACTIONS } from "./calculation-properties";

export interface AdditionProperties {
  minSum: number;
  maxSum: number;
  secondAddendRounding: number;
  includeZeroOnOperand: boolean;
  transgression: number;
}

export const initialAdditionProperties = {
  minSum: 0,
  maxSum: 10,
  secondAddendRounding: 1,
  includeZeroOnOperand: true,
  transgression: UNLIMITED_TRANSACTIONS
} as AdditionProperties;
