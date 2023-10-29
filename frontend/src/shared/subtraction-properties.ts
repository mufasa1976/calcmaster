import { UNLIMITED_TRANSACTIONS } from "./constants";

export interface SubtractionProperties {
  maxDifference: number;
  subtrahendRounding: number;
  transgression: number;
  minSubtrahend: number;
}

export const initialSubtractionProperties = {
  maxDifference: 10,
  subtrahendRounding: 1,
  transgression: UNLIMITED_TRANSACTIONS,
  minSubtrahend: 0
} as SubtractionProperties;
