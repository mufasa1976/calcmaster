package io.github.mufasa1976.calcmaster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationPropertiesImpl.class)
@EnableAsync
public class Application {
  public static void main(String... args) {
    new SpringApplication(Application.class).run(args);
  }
}
