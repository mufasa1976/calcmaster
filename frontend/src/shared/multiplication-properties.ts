import { UNLIMITED_TRANSACTIONS } from "./calculation-properties";

export interface MultiplicationProperties {
  maxProduct: number;
  exclusions: number[];
  fixedMultiplicands: number[];
  transgression: number;
}

export const initialMultiplicationProperties = {
  maxProduct: 100,
  exclusions: [],
  fixedMultiplicands: [],
  transgression: UNLIMITED_TRANSACTIONS
} as MultiplicationProperties;
