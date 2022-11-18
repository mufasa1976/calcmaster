package io.github.mufasa1976.calcmaster.records;

import java.util.List;

public record DivisionProperties(int maxDividend, int maxRemainder, List<Integer> exclusions, List<Integer> fixedDivisors) {
}
