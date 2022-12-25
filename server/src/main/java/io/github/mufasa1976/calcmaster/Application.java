package io.github.mufasa1976.calcmaster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationPropertiesImpl.class)
public class Application {
  public static void main(String... args) {
    new SpringApplication(Application.class).run(args);
  }
}
