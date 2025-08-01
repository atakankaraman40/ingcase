package com.ingcase.digitalwallet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PaymentNotAllowedException extends RuntimeException {
  public PaymentNotAllowedException(String message) {
    super(message);
  }
}