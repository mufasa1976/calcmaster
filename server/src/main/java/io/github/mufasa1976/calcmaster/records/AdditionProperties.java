package io.github.mufasa1976.calcmaster.records;

import lombok.Builder;

@Builder
public record AdditionProperties(int maxSum, int secondAddendRounding, int transgression, int minOperand) {}
