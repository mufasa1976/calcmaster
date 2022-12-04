export interface MultiplicationProperties {
  maxProduct: number;
  exclusions: number[];
  fixedMultiplicands: number[];
}

export const initialMultiplicationProperties = {
  maxProduct: 100,
  exclusions: [],
  fixedMultiplicands: []
} as MultiplicationProperties;
