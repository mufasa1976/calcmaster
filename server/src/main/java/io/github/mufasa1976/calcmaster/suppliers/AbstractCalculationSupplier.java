package io.github.mufasa1976.calcmaster.suppliers;

import io.github.mufasa1976.calcmaster.dtos.Calculation;
import io.github.mufasa1976.calcmaster.enums.HiddenField;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@RequiredArgsConstructor
public abstract class AbstractCalculationSupplier implements CalculationSupplier {
  protected final Random random;
  protected final boolean toggleHide;
  protected final int maxTriesToGetDistinctOperationTuples;

  protected Set<Operation> operationTuples = new HashSet<>();

  @Override
  public void init() {}

  @Override
  public final Calculation get() {
    Calculation calculation = getInternal();
    for (int i = 0; existsOperationTuple(calculation.getOperand1(), calculation.getOperand2()) && i <= maxTriesToGetDistinctOperationTuples; i++) {
      calculation = getInternal();
    }
    addOperationTuple(calculation.getOperand1(), calculation.getOperand2());
    return calculation;
  }

  protected abstract Calculation getInternal();

  protected HiddenField getRandomHiddenField() {
    return toggleHide ? HiddenField.values()[random.nextInt(HiddenField.values().length)] : HiddenField.RESULT;
  }

  protected boolean existsOperationTuple(long operand1, long operand2) {
    return operationTuples.contains(new Operation(operand1, operand2));
  }

  protected void addOperationTuple(long operand1, long operand2) {
    operationTuples.add(new Operation(operand1, operand2));
  }

  protected long getValue(int[] digits) {
    long value = 0;
    for (int digit = 0; digit < digits.length; digit++) {
      value += Math.pow(10, digits.length - digit - 1) * digits[digit];
    }
    return value;
  }

  @Value
  private static class Operation implements Comparable<Operation> {
    long operand1, operand2;

    @Override
    public int compareTo(Operation o) {
      int result = (int) Math.signum(this.operand1 - o.operand1);
      if (result != 0) {
        return result;
      }
      return (int) Math.signum(this.operand2 - o.operand2);
    }
  }
}
