package io.github.mufasa1976.calcmaster.controllers;

import io.github.mufasa1976.calcmaster.records.CalculationProperties;
import io.github.mufasa1976.calcmaster.services.CalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class CalculationController {
  private static final String FILENAME_PREFIX = "calculations_";
  private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
  private final CalculationService calculationService;

  @PostMapping(path = "/api/v1/calculation.{fileEnding}")
  public ResponseEntity<StreamingResponseBody> generateCalculationReportAsPDF(
      Locale locale,
      @RequestBody @NotNull @Valid CalculationProperties calculationProperties,
      @PathVariable("fileEnding") String fileEnding) {
    final var outputFormat = CalculationService.OutputFormat.parseFileEnding(fileEnding)
                                                            .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_ACCEPTABLE, "File Ending " + fileEnding + " is not supported"));
    final var fileName = FILENAME_PREFIX + TIMESTAMP_FORMATTER.format(LocalDateTime.now()) + "." + outputFormat.getFileEnding();
    return calculationService.createCalculations(calculationProperties, locale)
                             .map(calculations -> ResponseEntity.ok()
                                                                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                                                                .contentType(outputFormat.getMediaType())
                                                                .body(calculationService.printCalculations(calculations, locale, outputFormat)))
                             .orElseGet(ResponseEntity.notFound()::build);
  }
}
