package io.github.mufasa1976.calcmaster.dtos;

import io.github.mufasa1976.calcmaster.enums.HiddenField;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

@Value
@Builder
public class Calculation implements Comparable<Calculation> {
  public enum Type {
    CALCULATION, ROUNDING, CONVERSION
  }

  Type type;
  long operand1;
  String operand1Unit;
  String operator;
  long operand2;
  long result;
  String resultUnit;
  Long remainder;
  HiddenField hiddenField;
  String textExercise;
  String textSolution;
  double conversionFactor;

  @Override
  public String toString() {
    if (type == Type.CONVERSION) {
      if (StringUtils.isNotEmpty(textExercise)) {
        return String.format("%s = %d%s", textExercise, result, resultUnit);
      }
      return String.format("%d%s = %d%s", operand1, operand1Unit, result, resultUnit);
    }

    if (StringUtils.isNotBlank(textSolution)) {
      return textSolution;
    } else if (StringUtils.isNotBlank(textExercise)) {
      return textExercise;
    }
    return String.format("%d %s %d = %d", operand1, operator, operand2, result);
  }

  @Override
  public int compareTo(Calculation o) {
    int result = (int) Math.signum(this.operand1 - o.operand1);
    if (result != 0) {
      return result;
    }
    result = (int) Math.signum(this.operand2 - o.operand2);
    if (result != 0) {
      return result;
    }
    return StringUtils.compare(this.operator, o.operator);
  }
}
