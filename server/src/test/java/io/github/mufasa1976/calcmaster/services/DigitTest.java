package io.github.mufasa1976.calcmaster.services;

public interface DigitTest {
  default int getDigit(long value, int digit) {
    return (int) (value % (int) Math.pow(10, digit)) / (int) Math.pow(10, digit - 1);
  }
}
