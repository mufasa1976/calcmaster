import { UNLIMITED_TRANSACTIONS } from "./constants";

export interface AdditionProperties {
  maxSum: number;
  secondAddendRounding: number;
  transgression: number;
  minOperand: number;
}

export const initialAdditionProperties = {
  maxSum: 10,
  secondAddendRounding: 1,
  transgression: UNLIMITED_TRANSACTIONS,
  minOperand: 0
} as AdditionProperties;
