package io.github.mufasa1976.calcmaster.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfiguration implements AsyncConfigurer {
  @Override
  public Executor getAsyncExecutor() {
    return asyncTaskExecutor();
  }

  @Bean
  @ConfigurationProperties(prefix = "application.task-executor")
  public AsyncTaskExecutor asyncTaskExecutor() {
    return new ThreadPoolTaskExecutor();
  }
}
