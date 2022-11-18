package io.github.mufasa1976.calcmaster;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application")
@Data
public class ApplicationPropertiesImpl implements ApplicationProperties {
  private int maxNumberOfCalculations;
  private int maxTriesToFindSumOfAdditionNotEqualToSecondAddend;
  private int maxTriesToGetDistinctOperationTuples;
}
