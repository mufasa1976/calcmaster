package io.github.mufasa1976.calcmaster.dtos;

import io.github.mufasa1976.calcmaster.enums.HiddenField;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

@Value
@Builder
public class Calculation implements Comparable<Calculation> {
  int operand1;
  String operator;
  int operand2;
  int result;
  Integer remainder;
  HiddenField hiddenField;
  String textExercise;
  String textSolution;

  @Override
  public String toString() {
    if (StringUtils.isNotBlank(textSolution)) {
      return textSolution;
    } else if (StringUtils.isNotBlank(textExercise)) {
      return textExercise;
    }
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
