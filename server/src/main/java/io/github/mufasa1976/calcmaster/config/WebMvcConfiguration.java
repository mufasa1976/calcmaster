package io.github.mufasa1976.calcmaster.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.servlet.function.RequestPredicates.GET;
import static org.springframework.web.servlet.function.RouterFunctions.route;

@Configuration
@EnableWebMvc
public class WebMvcConfiguration implements WebMvcConfigurer {
  private static final String[] ANGULAR_RESOURCES = {
      "/favicon.ico",
      "/main.*.js",
      "/polyfills.*.js",
      "/runtime.*.js",
      "/styles.*.css",
      "/deeppurple-amber.css",
      "/indigo-pink.css",
      "/pink-bluegrey.css",
      "/purple-green.css",
      "/3rdpartylicenses.txt"
  };
  private static final List<Locale> SUPPORTED_LANGUAGES = List.of(Locale.GERMAN, Locale.ENGLISH);
  private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

  private final String prefix;
  private final Collection<HttpMessageConverter<?>> messageConverters;
  private final AsyncTaskExecutor asyncTaskExecutor;

  public WebMvcConfiguration(
      @Value("${spring.thymeleaf.prefix:" + ThymeleafProperties.DEFAULT_PREFIX + "}") String prefix,
      Collection<HttpMessageConverter<?>> messageConverters,
      AsyncTaskExecutor asyncTaskExecutor) {
    this.prefix = StringUtils.appendIfMissing(prefix, "/");
    this.messageConverters = messageConverters;
    this.asyncTaskExecutor = asyncTaskExecutor;
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.setOrder(1);
    registry.addResourceHandler("/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/")
            .resourceChain(true);
    for (Locale language : SUPPORTED_LANGUAGES) {
      final var relativeAngularResources = Stream.of(ANGULAR_RESOURCES)
                                                 .filter(resource -> StringUtils.contains(resource, "*"))
                                                 .map(resource -> "/" + language.getLanguage() + resource)
                                                 .toArray(String[]::new);
      registry.addResourceHandler(relativeAngularResources)
              .addResourceLocations(prefix + language.getLanguage() + "/");

      final var fixedAngularResources = Stream.of(ANGULAR_RESOURCES)
                                              .filter(resource -> !StringUtils.contains(resource, "*"))
                                              .map(resource -> "/" + language.getLanguage() + resource)
                                              .toArray(String[]::new);
      registry.addResourceHandler(fixedAngularResources)
              .addResourceLocations(prefix);

      registry.addResourceHandler("/" + language.getLanguage() + "/assets/**")
              .addResourceLocations(prefix + language.getLanguage() + "/assets/");
    }
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.setOrder(2);
    for (Locale language : SUPPORTED_LANGUAGES) {
      registry.addViewController("/" + language.getLanguage() + "/**").setViewName(language.getLanguage() + "/index");
    }
  }

  @Bean
  public RouterFunction<ServerResponse> routerFunction() {
    return route(GET("/"), this::defaultLandingPage);
  }

  private ServerResponse defaultLandingPage(ServerRequest request) {
    final var locale = Optional.ofNullable(Locale.lookup(request.headers().acceptLanguage(), SUPPORTED_LANGUAGES))
                               .orElse(DEFAULT_LOCALE);
    return ServerResponse.status(HttpStatus.TEMPORARY_REDIRECT).render("redirect:/" + locale.getLanguage());
  }

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer.defaultContentType(APPLICATION_JSON);
  }

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.addAll(messageConverters);
  }

  @Override
  public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
    configurer.setDefaultTimeout(5000).setTaskExecutor(asyncTaskExecutor);
  }
}
