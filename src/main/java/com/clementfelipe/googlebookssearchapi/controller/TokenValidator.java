package com.clementfelipe.googlebookssearchapi.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenValidator {
  private static final Pattern bearerTokenRegex = Pattern.compile("^Bearer (.+)$");

  public static String validateAndParse(String token) {
    final Matcher tokenMatcher = bearerTokenRegex.matcher(token);
    
    if (token == null || token.trim().isEmpty() || !tokenMatcher.find()) {
      throw new SecurityException("invalid or missing access token");
    }

    return tokenMatcher.group(1);
  }
}
