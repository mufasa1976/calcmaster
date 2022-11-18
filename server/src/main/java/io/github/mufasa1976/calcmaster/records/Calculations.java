package io.github.mufasa1976.calcmaster.records;

import lombok.Builder;
import io.github.mufasa1976.calcmaster.dtos.Calculation;

import java.util.List;
import java.util.Optional;

@Builder
public record Calculations(Optional<String> subheader, List<Calculation> calculations, boolean verticalDisplay) {
}
