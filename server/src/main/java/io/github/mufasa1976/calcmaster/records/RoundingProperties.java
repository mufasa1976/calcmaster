package io.github.mufasa1976.calcmaster.records;

import lombok.Builder;

@Builder
public record RoundingProperties(int maxPower, int minPower) {}
