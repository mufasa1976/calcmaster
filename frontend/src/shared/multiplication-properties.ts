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
  transgression: -1
} as MultiplicationProperties;
