package io.github.mufasa1976.calcmaster.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import io.github.mufasa1976.calcmaster.config.WebMvcConfiguration;

import java.util.Arrays;
import java.util.Locale;

@Controller
public class IndexController {
  private static final String DEFAULT_LANDING_PAGE = "en/index";

  @GetMapping
  public String landingPage(final Locale locale) {
    return "redirect:/" + locale.getCountry();
  }

  @GetMapping("{language}")
  public String localeSpecificLandingPage(@PathVariable("language") final String language) {
    if (Arrays.asList(WebMvcConfiguration.OFFERED_ANGULAR_LANGUAGES).contains(language)) {
      return language + "/index";
    }
    return DEFAULT_LANDING_PAGE;
  }
}
