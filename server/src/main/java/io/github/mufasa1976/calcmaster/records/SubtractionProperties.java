package io.github.mufasa1976.calcmaster.records;

import lombok.Builder;

@Builder
public record SubtractionProperties(int minDifference, int maxDifference, int subtrahendRounding, boolean includeZeroOnOperand, int transgression) {}
