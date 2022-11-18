package io.github.mufasa1976.calcmaster.records;

import java.util.List;

public record MultiplicationProperties(int maxProduct, List<Integer> exclusions, List<Integer> fixedMultiplicands) {
}
