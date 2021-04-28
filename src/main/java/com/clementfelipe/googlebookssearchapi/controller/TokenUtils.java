package com.clementfelipe.googlebookssearchapi.controller;

public class TokenUtils {
  public static String removeTokenPrefix(String token) {
    if (token == null || token.trim().isEmpty()) {
      return null;
    }

    return token.split(" ")[1];
  }
}
