package io.github.mufasa1976.calcmaster.services;

import io.github.mufasa1976.calcmaster.ApplicationProperties;
import io.github.mufasa1976.calcmaster.records.CalculationProperties;
import io.github.mufasa1976.calcmaster.records.Calculations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.export.*;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceResourceBundle;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalculationServiceImpl implements CalculationService {
  private static final String PARAMETER_SUBHEADER = "SUBHEADER";
  private static final String PARAMETER_VERTICAL_DISPLAY = "VERTICAL_DISPLAY";
  private static final String EMPTY_STRING = "";

  private final ApplicationProperties applicationProperties;
  private final ResourceLoader resourceLoader;
  private final MessageSource messageSource;

  private JasperReport calculationReport;
  private JasperReport solutionsReport;

  @PostConstruct
  private void init() throws IOException, JRException {
    final Resource calculationReportResource = resourceLoader.getResource("classpath:/calculations.jrxml");
    try (final InputStream inputStream = calculationReportResource.getInputStream()) {
      this.calculationReport = JasperCompileManager.compileReport(inputStream);
    }

    final Resource solutionReportResource = resourceLoader.getResource("classpath:/solutions.jrxml");
    try (final InputStream inputStream = solutionReportResource.getInputStream()) {
      this.solutionsReport = JasperCompileManager.compileReport(inputStream);
    }
  }

  @Override
  public Mono<Calculations> createCalculations(CalculationProperties calculationProperties, Locale locale) {
    return Mono.justOrEmpty(new Calculator(calculationProperties, applicationProperties, messageSource, locale).calculate());
  }

  @Override
  public Flux<DataBuffer> printCalculations(Calculations calculations, Locale locale, OutputFormat outputFormat, DataBufferFactory dataBufferFactory) {
    final Map<String, Object> parameters = new HashMap<>();
    parameters.put(JRParameter.REPORT_LOCALE, locale);
    parameters.put(JRParameter.REPORT_RESOURCE_BUNDLE, new MessageSourceResourceBundle(messageSource, locale));
    parameters.put(PARAMETER_SUBHEADER, calculations.subheader().orElse(EMPTY_STRING));
    parameters.put(PARAMETER_VERTICAL_DISPLAY, calculations.verticalDisplay());

    try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         final OutputStream outputStream = new BufferedOutputStream(byteArrayOutputStream)) {
      final JRDataSource calculationsDataSource = new JRBeanCollectionDataSource(calculations.calculations());
      final JasperPrint calculationReport = JasperFillManager.fillReport(this.calculationReport, parameters, calculationsDataSource);

      final JRDataSource solutionsDataSource = new JRBeanCollectionDataSource(calculations.calculations());
      final JasperPrint solutionsReport = JasperFillManager.fillReport(this.solutionsReport, parameters, solutionsDataSource);

      final var exporter = switch (outputFormat) {
        case PDF -> createPdfExporter(calculationReport, solutionsReport);
        case DOCX -> createDocxExporter(calculationReport, solutionsReport);
      };
      exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
      exporter.exportReport();
      return Flux.just(dataBufferFactory.wrap(byteArrayOutputStream.toByteArray()));
    } catch (IOException | JRException e) {
      log.error("Error while generating Calculation Report", e);
      return Flux.error(e);
    }
  }

  private Exporter<ExporterInput, PdfReportConfiguration, PdfExporterConfiguration, OutputStreamExporterOutput> createPdfExporter(JasperPrint... reports) {
    final var exporter = new JRPdfExporter();
    exporter.setExporterInput(SimpleExporterInput.getInstance(Arrays.asList(reports)));

    final var reportConfiguration = new SimplePdfReportConfiguration();
    reportConfiguration.setSizePageToContent(true);
    reportConfiguration.setForceLineBreakPolicy(false);
    exporter.setConfiguration(reportConfiguration);

    final var exporterConfiguration = new SimplePdfExporterConfiguration();
    exporterConfiguration.setMetadataAuthor("Author");
    exporter.setConfiguration(exporterConfiguration);

    return exporter;
  }

  private Exporter<ExporterInput, DocxReportConfiguration, DocxExporterConfiguration, OutputStreamExporterOutput> createDocxExporter(JasperPrint... reports) {
    final var exporter = new JRDocxExporter();
    exporter.setExporterInput(SimpleExporterInput.getInstance(Arrays.asList(reports)));

    final var reportConfiguration = new SimpleDocxExporterConfiguration();
    exporter.setConfiguration(reportConfiguration);

    final var exporterConfiguration = new SimpleDocxExporterConfiguration();
    exporterConfiguration.setMetadataAuthor("Author");
    exporter.setConfiguration(exporterConfiguration);

    return exporter;
  }
}
