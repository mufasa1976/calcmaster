package io.github.mufasa1976.calcmaster;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlTemplate;

@SpringBootTest(properties = "spring.main.banner-mode=off")
@AutoConfigureMockMvc
@DisplayName("Test the Landing Page")
class LandingPageTest {
  @Autowired
  private MockMvc web;

  @Test
  @DisplayName("GET / and expect redirected to /en")
  void defaultLandingPage() throws Exception {
    web.perform(get("/"))
       .andExpect(redirectedUrlTemplate("/en"));
  }

  @Test
  @DisplayName("GET / with Headers [Accept-Language:\"de\"] and expect redirected to /de")
  void defaultLandingPageWithGermanLocale() throws Exception {
    web.perform(get("/").locale(Locale.GERMAN))
       .andExpect(redirectedUrlTemplate("/de"));
  }
}
