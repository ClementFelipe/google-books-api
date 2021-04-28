package com.clementfelipe.googlebookssearchapi.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {
  @ExceptionHandler(value = { Exception.class })
  protected ResponseEntity<Object> handleConflict(Exception ex, WebRequest request) {
    if (ex instanceof SecurityException) {
      return handleExceptionInternal(ex, "invalid credentials", new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    if (ex instanceof IllegalStateException) {
      return handleExceptionInternal(ex, "invalid google user token", new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    return handleExceptionInternal(ex, "unknown error", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }
}