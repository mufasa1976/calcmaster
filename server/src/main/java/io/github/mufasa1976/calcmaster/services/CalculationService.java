package io.github.mufasa1976.calcmaster.services;

import io.github.mufasa1976.calcmaster.records.CalculationProperties;
import io.github.mufasa1976.calcmaster.records.Calculations;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.Locale;
import java.util.Optional;

public interface CalculationService {

  @RequiredArgsConstructor
  @Getter
  enum OutputFormat {
    PDF("pdf", MediaType.APPLICATION_PDF),
    DOCX("docx", MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));

    private final String fileEnding;
    private final MediaType mediaType;

    public static Optional<OutputFormat> parseFileEnding(String fileEnding) {
      for (OutputFormat outputFormat : values()) {
        if (outputFormat.fileEnding.equals(fileEnding)) {
          return Optional.of(outputFormat);
        }
      }
      return Optional.empty();
    }
  }

  Optional<Calculations> createCalculations(final CalculationProperties calculationProperties, final Locale locale);

  StreamingResponseBody printCalculations(Calculations calculations, Locale locale, OutputFormat outputFormat);
}
