package io.github.mufasa1976.calcmaster.records;

import lombok.Builder;

import java.util.List;

@Builder
public record MultiplicationProperties(int maxProduct, List<Integer> exclusions, List<Integer> fixedMultiplicands, int transgression) {}
