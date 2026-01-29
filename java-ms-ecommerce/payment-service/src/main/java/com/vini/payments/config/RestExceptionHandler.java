package com.vini.payments.config;

import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ProblemDetail validation(MethodArgumentNotValidException ex) {
    var pd = ProblemDetail.forStatus(400);
    pd.setTitle("Validation error");
    pd.setDetail(ex.getBindingResult().getFieldErrors().stream()
        .map(f -> f.getField() + ": " + f.getDefaultMessage())
        .findFirst().orElse("Invalid request"));
    return pd;
  }

  @ExceptionHandler(RuntimeException.class)
  ProblemDetail runtime(RuntimeException ex) {
    var pd = ProblemDetail.forStatus(500);
    pd.setTitle("Internal error");
    pd.setDetail(ex.getMessage());
    return pd;
  }
}
