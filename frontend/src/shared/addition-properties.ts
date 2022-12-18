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
  transgression: -1
} as AdditionProperties;
