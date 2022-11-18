package io.github.mufasa1976.calcmaster.dtos;

import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;
import io.github.mufasa1976.calcmaster.enums.HiddenField;

@Value
@Builder
public class Calculation implements Comparable<Calculation> {
  int operand1;
  String operator;
  int operand2;
  int result;
  Integer remainder;
  HiddenField hiddenField;

  @Override
  public String toString() {
    return String.format("%d %s %d = %d", operand1, operator, operand2, result);
  }

  @Override
  public int compareTo(Calculation o) {
    int result = this.operand1 - o.operand1;
    if (result != 0) {
      return result;
    }
    result = this.operand2 - o.operand2;
    if (result != 0) {
      return result;
    }
    return StringUtils.compare(this.operator, o.operator);
  }
}
