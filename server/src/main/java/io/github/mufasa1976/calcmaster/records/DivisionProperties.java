package io.github.mufasa1976.calcmaster.records;

import lombok.Builder;

import java.util.List;

@Builder
public record DivisionProperties(int maxDividend, int maxRemainder, List<Integer> exclusions, List<Integer> fixedDivisors) {}
