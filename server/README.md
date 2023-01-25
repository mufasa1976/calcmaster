# Calcmaster Backend

The Backend Application written with [Spring Boot](https://spring.io/projects/spring-boot) will be used for 2 Aspects:

* serve the REST-APIs
* serve the Angular Application as static Files

## serving Frontend Application

### Dependencies and Thymeleaf Configuration

The Angular Application will be provided as a [Webjar](https://webjars.org/). The File [`index.html`](../frontend/src/index.html)
has been changed to be a [Thymeleaf natural Template](https://thymeleaf.org/).

This is the Reason why you need the Dependency `spring-boot-starter-thymeleaf`

```xml

<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
  </dependency>
</dependencies>
```

The Webjar of the Frontend Application will be included as a Dependency:

```xml

<dependencies>
  <dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>calcmaster-frontend</artifactId>
    <version>${project.version}</version>
  </dependency>
</dependencies>
```

Because sometimes it would be necessary to build the Backend without the Frontend-Application (i.E. just to test the REST-API) one
can disable building and linking the Frontend-Application by using Option `-DskipFrontend`.

Beside of the Dependencies you have to redefine the Path, where Thymeleaf would look for its Templates inside
[`application.yml`](src/main/resources/application.yml):

```yaml
spring:
  thymeleaf:
    prefix: "classpath:/META-INF/resources/frontend/"
```

If you want to deploy the Application with a Context-Path of your like you have to enable the ForwardedHeaderFilter
within your [`application.yml`](src/main/resources/application.yml):

```yaml
server:
  forward-headers-strategy: framework
```

### WebMvcConfiguration for multilingual Angular Application

Angular Application are bookmarkable. That means, that any missing Link (HTTP 404) must return the Index-Page so that the Angular
Application will handle the HTTP 404 correctly. \
You can define this Behaviour with the [`WebMvcConfigurer`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/config/annotation/WebMvcConfigurer.html):

```java
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

  public WebMvcConfiguration(@Value("${spring.thymeleaf.prefix:" + ThymeleafProperties.DEFAULT_PREFIX + "}") String prefix) {
    this.prefix = StringUtils.appendIfMissing(prefix, "/");
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.setOrder(1);
    registry.addResourceHandler("/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/")
            .resourceChain(true);
    SUPPORTED_LANGUAGES.forEach(registerLocalizedAngularResourcesTo(registry));
  }

  private Consumer<Locale> registerLocalizedAngularResourcesTo(ResourceHandlerRegistry registry) {
    return language -> {
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
    };
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.setOrder(2);
    SUPPORTED_LANGUAGES.forEach(language -> registry.addViewController("/" + language.getLanguage() + "/**").setViewName(language.getLanguage() + "/index"));
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
}
```

Here you have several Aspects to recognize:

1. All Angular-Resources will be served first. (`registry.setOrder(1)`)
2. only if this is not an Angular-Resource (or a REST-API) the `index.html` will be served (`registry.setOrder(2)`)
3. the Resources must be distinguished between relative Resources (with `*`) or fixed Resources (without `*`)
4. all Languages, which will be provided by the Frontend Application must be available on `SUPPORTED_LANGUAGES`

### WebMvcConfiguration for uni-lingual Angular Application

If you don't have a multilingual Angular Application the `WebMvcConfiguration` would be a bit easier:

```java

@Configuration
@EnableWebMvc
public class WebMvcConfiguration implements WebMvcConfigurer {
  private static final String[] ANGULAR_RESOURCES = {
      "/favicon.ico",
      "/main.*.js",
      "/main-*.*.js",
      "/polyfills.*.js",
      "/polyfills-*.*.js",
      "/runtime.*.js",
      "/runtime-*.*.js",
      "/styles.*.css",
      "/deeppurple-amber.css",
      "/indigo-pink.css",
      "/pink-bluegrey.css",
      "/purple-green.css",
      "/3rdpartylicenses.txt"
  };

  private final String prefix;

  public WebMvcConfiguration(@Value("${spring.thymeleaf.prefix:" + ThymeleafProperties.DEFAULT_PREFIX + "}") String prefix) {
    this.prefix = StringUtils.appendIfMissing(prefix, "/");
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.setOrder(1);
    registry.addResourceHandler("/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/")
            .resourceChain(true);
    registry.addResourceHandler(ANGULAR_RESOURCES)
            .addResourceLocations(prefix);
    registry.addResourceHandler("/assets/**")
            .addResourceLocations(prefix + "assets/");
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.setOrder(2);
    registry.addViewController("/**").setViewName("index");
  }
}
```

### WebFluxConfiguration for multilingual Angular Application

If you use Spring Reactive you have to implement an Implementation of `WebFluxConfiguration`

```java
@Configuration
@EnableWebFlux
public class WebFluxConfiguration implements WebFluxConfigurer {
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
  private static final Locale DEFAULT_LANGUAGE = Locale.ENGLISH;

  private final String prefix;
  private final ThymeleafReactiveViewResolver thymeleafReactiveViewResolver;

  public WebFluxConfiguration(@Value("${spring.thymeleaf.prefix:" + ThymeleafProperties.DEFAULT_PREFIX + "}") String prefix, ThymeleafReactiveViewResolver thymeleafReactiveViewResolver) {
    this.prefix = StringUtils.appendIfMissing(prefix, "/");
    this.thymeleafReactiveViewResolver = thymeleafReactiveViewResolver;
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/")
            .resourceChain(true);
    SUPPORTED_LANGUAGES.forEach(language -> registry.addResourceHandler("/" + language.getLanguage() + "/**")
                                                    .addResourceLocations(prefix + language.getLanguage() + "/"));
  }

  @Override
  public void configureViewResolvers(ViewResolverRegistry registry) {
    registry.viewResolver(thymeleafReactiveViewResolver);
  }

  @Bean
  public RouterFunction<ServerResponse> routerFunction() {
    final var routerFunctionBuilder = route().GET("/", this::defaultLandingPage);
    SUPPORTED_LANGUAGES.forEach(addLocalizedLandingPageTo(routerFunctionBuilder));
    return routerFunctionBuilder.build();
  }

  private Mono<ServerResponse> defaultLandingPage(ServerRequest request) {
    final var locale = Optional.ofNullable(Locale.lookup(request.headers().acceptLanguage(), SUPPORTED_LANGUAGES))
                               .orElse(DEFAULT_LANGUAGE);
    return ServerResponse.temporaryRedirect(request.uriBuilder().path(locale.getLanguage()).build()).build();
  }

  private Consumer<Locale> addLocalizedLandingPageTo(RouterFunctions.Builder routerFunctionBuilder) {
    return language -> {
      var requestPredicate = Stream.of(ANGULAR_RESOURCES)
                                   .map(angularResource -> "/" + language.getLanguage() + angularResource)
                                   .reduce(GET("/" + language.getLanguage() + "/**"), (predicate, route) -> predicate.and(GET(route).negate()), RequestPredicate::and);
      requestPredicate = requestPredicate.and(GET("/" + language.getLanguage() + "/assets/**").negate());
      routerFunctionBuilder.route(requestPredicate, request -> ServerResponse.ok()
                                                                             .contentType(MediaType.TEXT_HTML)
                                                                             .render(language.getLanguage() + "/index"));
    };
  }
}
```

Within Spring Reactive we have no `WebFluxConfiguration.addViewController()` Method. Instead we have to instrument
the `RouterFunction` to exclude the defined Resources and return `index.html` only if this is an undefined Resource.

I assume that there is no `RestController` defined under any Language-specific Context-Path
(i.e. no `/en/api/v1/...` nor `/de/api/v1/...`). If you have these Constellation you also have to
exclude these Paths within the `RouterFunction`.

### WebFluxConfiguration for uni-lingual Angular Application

As for the uni-lingual [`WebMvcConfiguration`](#webmvcconfiguration-for-uni-lingual-angular-application) the
`WebFluxConfiguration` for a uni-lingual Angular Application is much simpler:

```java
@Configuration
@EnableWebFlux
public class WebFluxConfiguration implements WebFluxConfigurer {
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
  private final String prefix;
  private final ThymeleafReactiveViewResolver thymeleafReactiveViewResolver;

  public WebFluxConfiguration(@Value("${spring.thymeleaf.prefix:" + ThymeleafProperties.DEFAULT_PREFIX + "}") String prefix, ThymeleafReactiveViewResolver thymeleafReactiveViewResolver) {
    this.prefix = StringUtils.appendIfMissing(prefix, "/");
    this.thymeleafReactiveViewResolver = thymeleafReactiveViewResolver;
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/")
            .resourceChain(true);
    registry.addResourceHandler("/**")
            .addResourceLocations(prefix);
  }

  @Override
  public void configureViewResolvers(ViewResolverRegistry registry) {
    registry.viewResolver(thymeleafReactiveViewResolver);
  }

  @Bean
  public RouterFunction<ServerResponse> routerFunction() {
    final var requestPredicate = Stream.concat(
        Stream.of(ANGULAR_RESOURCES),
        Stream.of("/assets/**", "/api/**", "/webjars/**")
    ).reduce(GET("/**"), (predicate, route) -> predicate.and(GET(route).negate()), RequestPredicate::and);
    return route(requestPredicate, request -> ServerResponse.ok()
                                                            .contentType(MediaType.TEXT_HTML)
                                                            .render("index"));
  }
}
```

Just as [`WebFluxConfiguration` for multilingual Application](#webfluxconfiguration-for-multilingual-angular-application)
I assume that the `RestController` will be defined under the Context-Path `/api/**`. So we have to exclude these
Paths before routing to `index.html`.

We also have to separately exclude `/assets/**` and `/webjars/**` so these two will also be handled by the `ResourceHandler`. 

### Landing Page

The Frontend Application will be provided with the Language as a Path-Prefix. For Example [the german Version](https://calcmaster.coolstuff.software/de) or
[the english Version](https://calcmaster.coolstuff.software/en). But if you don't know the Landing Page will be without any Language Prefix.
(i.E.: [https://calcmaster.coolstuff.software](https://calcmaster.coolstuff.software)).

In this case the Application has a [`LandingPageController`](src/main/java/io/github/mufasa1976/calcmaster/controllers/LandingPageController.java) which gets the
Default-Locale of the Browser and redirects to the correct localized Landing-Page:

```java

@Controller
public class LandingPageController {
  private static final String DEFAULT_REDIRECT = "redirect:/en";

  @GetMapping
  public String landingPage(final Locale locale) {
    if (Arrays.asList(WebMvcConfiguration.SUPPORTED_LANGUAGES).contains(locale.getLanguage())) {
      return "redirect:/" + locale.getLanguage();
    }
    return DEFAULT_REDIRECT;
  }
}
```

If the Default-Locale of the Browser isn't a supported Version one will be redirected to the english Version of the Frontend Application.