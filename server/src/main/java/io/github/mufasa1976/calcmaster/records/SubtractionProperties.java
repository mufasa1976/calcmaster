package io.github.mufasa1976.calcmaster.records;

import lombok.Builder;

@Builder
public record SubtractionProperties(int maxDifference, int subtrahendRounding, int transgression, int minSubtrahend) {}
