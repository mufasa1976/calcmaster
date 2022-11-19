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

### WebMvcConfiguration

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
  public static final String[] OFFERED_ANGULAR_LANGUAGES = {"de", "en"};

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
    for (String offeredLanguage : OFFERED_ANGULAR_LANGUAGES) {
      final var relativeAngularResources = Stream.of(ANGULAR_RESOURCES)
                                                 .filter(resource -> StringUtils.contains(resource, "*"))
                                                 .map(resource -> "/" + offeredLanguage + resource)
                                                 .toArray(String[]::new);
      registry.addResourceHandler(relativeAngularResources)
              .addResourceLocations(prefix + offeredLanguage + "/");

      final var fixedAngularResources = Stream.of(ANGULAR_RESOURCES)
                                              .filter(resource -> !StringUtils.contains(resource, "*"))
                                              .map(resource -> "/" + offeredLanguage + resource)
                                              .toArray(String[]::new);
      registry.addResourceHandler(fixedAngularResources)
              .addResourceLocations(prefix);

      registry.addResourceHandler("/" + offeredLanguage + "/assets/**")
              .addResourceLocations(prefix + offeredLanguage + "/assets/");
    }
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.setOrder(2);
    for (String offeredLanguage : OFFERED_ANGULAR_LANGUAGES) {
      registry.addViewController(SLASH + offeredLanguage + "/**").setViewName(offeredLanguage + "/index");
    }
  }
}
```

Here you have several Aspects to recognize:

1. All Angular-Resources will be served first. (`registry.setOrder(1)`)
2. only if this is not an Angular-Resource (or a REST-API) the `index.html` will be served (`registry.setOrder(2)`)
3. the Resources must be distinguished between relative Resources (with `*`) or fixed Resources (without `*`)
4. all Languages, which will be provided by the Frontend Application must be available on `OFFERED_ANGULAR_LANGUAGES`

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
    if (Arrays.asList(WebMvcConfiguration.OFFERED_ANGULAR_LANGUAGES).contains(locale.getLanguage())) {
      return "redirect:/" + locale.getLanguage();
    }
    return DEFAULT_REDIRECT;
  }
}
```
If the Default-Locale of the Browser isn't a supported Version one will be redirected to the english Version of the Frontend Application.