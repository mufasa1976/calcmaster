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
  transgression: -1
} as SubtractionProperties;
