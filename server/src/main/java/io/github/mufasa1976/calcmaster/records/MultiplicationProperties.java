package io.github.mufasa1976.calcmaster.records;

import io.github.mufasa1976.calcmaster.enums.MultiplicationType;
import lombok.Builder;

import java.util.List;

@Builder
public record MultiplicationProperties(int maxProduct, List<Integer> exclusions, List<Integer> fixedMultiplicands, int transgression, MultiplicationType type) {}
