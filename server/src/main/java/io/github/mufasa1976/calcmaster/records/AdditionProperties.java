package io.github.mufasa1976.calcmaster.records;

import lombok.Builder;

@Builder
public record AdditionProperties(int minSum, int maxSum, int secondAddendRounding, boolean includeZeroOnOperand) {}
