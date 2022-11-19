package io.github.mufasa1976.calcmaster.controllers;

import io.github.mufasa1976.calcmaster.config.WebMvcConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.Locale;

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
