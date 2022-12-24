package io.github.mufasa1976.calcmaster;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.ACCEPT_LANGUAGE;

@SpringBootTest(properties = {"spring.main.banner-mode=off", "logging.level.root=debug"})
@AutoConfigureWebTestClient
@DisplayName("Test the Landing Page")
@Slf4j
class LandingPageTest {
  @Autowired
  private WebTestClient web;

  @Autowired
  private ResourceLoader resourceLoader;

  @Test
  @DisplayName("GET / and expect redirected to /en")
  void defaultLandingPage() {
    web.get()
       .uri("/")
       .exchange()
       .expectStatus().isTemporaryRedirect()
       .expectHeader().location("/en");
  }

  @Test
  @DisplayName("GET / with Headers [Accept-Language:\"de\"] and expect redirected to /de")
  void defaultLandingPageWithGermanLocale() {
    web.get()
       .uri("/")
       .header(ACCEPT_LANGUAGE, "de")
       .exchange()
       .expectStatus().isTemporaryRedirect()
       .expectHeader().location("/de");
  }

  @Test
  @DisplayName("GET /en/main.12345.js and expect the Content of classpath:/META-INF/resources/frontend/en/main.12345.js")
  void relativeResource() {
    web.get()
       .uri("/en/main.12345.js")
       .exchange()
       .expectStatus().isOk()
       .expectBody().consumeWith(response -> assertThat(response.getResponseBody())
           .asString()
           .isNotEmpty()
           .isEqualTo(getContent("classpath:/META-INF/resources/frontend/en/main.12345.js")));
  }

  private String getContent(String location) {
    final var resource = resourceLoader.getResource(location);
    if (!resource.exists() || !resource.isFile() || !resource.isReadable()) {
      return null;
    }
    try (final Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
      return FileCopyUtils.copyToString(reader);
    } catch (IOException e) {
      log.error("Error while reading Test-Resource {}", location, e);
      return null;
    }
  }

  @Test
  @DisplayName("GET /en/deeppurple-amber.css and expect the Content of classpath:/META-INF/resources/frontend/en/deeppurple-amber.css")
  void staticResource() {
    web.get()
       .uri("/en/deeppurple-amber.css")
       .exchange()
       .expectStatus().isOk()
       .expectBody().consumeWith(response -> assertThat(response.getResponseBody())
           .asString()
           .isNotEmpty()
           .isEqualTo(getContent("classpath:/META-INF/resources/frontend/en/deeppurple-amber.css")));
  }

  @Test
  @DisplayName("GET /en/assets/someText.txt and expect the Content of classpath:/META-INF/resources/frontend/en/assets/someText.txt")
  void assetsResource() {
    web.get()
       .uri("/en/assets/someText.txt")
       .exchange()
       .expectStatus().isOk()
       .expectBody().consumeWith(response -> assertThat(response.getResponseBody())
           .asString()
           .isNotEmpty()
           .isEqualTo(getContent("classpath:/META-INF/resources/frontend/en/assets/someText.txt")));
  }

  @Test
  @DisplayName("GET /en and expect the english index.html")
  void englishLandingPage() {
    web.get()
       .uri("/en")
       .exchange()
       .expectStatus().isOk()
       .expectBody().consumeWith(response -> assertThat(response.getResponseBody())
           .asString()
           .isNotEmpty()
           .isEqualTo(getContent("classpath:/META-INF/resources/frontend/en/index.html")));
  }
}
