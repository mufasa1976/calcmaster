export interface DivisionProperties {
  maxDividend: number;
  maxRemainder: number;
  exclusions: number[];
  fixedDivisors: number[];
}

export const initialDivisionProperties = {
  maxDividend: 100,
  maxRemainder: 0,
  exclusions: [],
  fixedDivisors: []
} as DivisionProperties;
