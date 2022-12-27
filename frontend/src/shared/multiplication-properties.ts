import { UNLIMITED_TRANSACTIONS } from "./constants";

export enum MultiplicationType {
  SQRT = "SQRT",
  HALF_OF_PRODUCT = "HALF_OF_PRODUCT"
}

export interface MultiplicationProperties {
  maxProduct: number;
  exclusions: number[];
  fixedMultiplicands: number[];
  transgression: number;
  type: MultiplicationType;
}

export const initialMultiplicationProperties = {
  maxProduct: 100,
  exclusions: [],
  fixedMultiplicands: [],
  transgression: UNLIMITED_TRANSACTIONS,
  type: MultiplicationType.SQRT
} as MultiplicationProperties;
