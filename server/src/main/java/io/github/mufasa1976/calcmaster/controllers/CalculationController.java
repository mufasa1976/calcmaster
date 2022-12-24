package io.github.mufasa1976.calcmaster.controllers;

import io.github.mufasa1976.calcmaster.records.CalculationProperties;
import io.github.mufasa1976.calcmaster.services.CalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Flux;

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
  public Flux<DataBuffer> generateCalculationReportAsPDF(
      Locale locale,
      @RequestBody @NotNull @Valid CalculationProperties calculationProperties,
      @PathVariable("fileEnding") String fileEnding,
      ServerHttpResponse response) {
    final var outputFormat = CalculationService.OutputFormat.parseFileEnding(fileEnding)
                                                            .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_ACCEPTABLE, "File Ending " + fileEnding + " is not supported"));
    final var fileName = FILENAME_PREFIX + TIMESTAMP_FORMATTER.format(LocalDateTime.now()) + "." + outputFormat.getFileEnding();
    response.getHeaders().setContentDisposition(ContentDisposition.attachment().filename(fileName).build());
    response.getHeaders().setContentType(outputFormat.getMediaType());
    return calculationService.createCalculations(calculationProperties, locale)
                             .flatMapMany(calculations -> calculationService.printCalculations(calculations, locale, outputFormat, response.bufferFactory()));
  }
}
