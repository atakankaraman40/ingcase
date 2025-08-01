package com.ingcase.digitalwallet.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private final String errorMessage = "Error: {} \nFor Request URL: {}";

    @ExceptionHandler({IllegalArgumentException.class,
            HandlerMethodValidationException.class,
            IllegalStateException.class,
            InsufficientBalanceException.class,
            TransferNotAllowedException.class,
            PaymentNotAllowedException.class})
    public ResponseEntity<Map<String, Object>> handleBadRequestExceptions(RuntimeException ex, HttpServletRequest req) {
        log.error(errorMessage, ex.getMessage(), req.getRequestURL().toString());
        Map<String, Object> errorBody = new LinkedHashMap<>();
        errorBody.put("status", HttpStatus.BAD_REQUEST.value());
        errorBody.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        errorBody.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
    }

    @ExceptionHandler({TransactionNotFoundException.class,
            WalletNotFoundException.class,
            CustomerNotFoundException.class})
    public ResponseEntity<Map<String, Object>> handleNotFoundExceptions(RuntimeException ex, HttpServletRequest req) {
        log.error(errorMessage, ex.getMessage(), req.getRequestURL().toString());
        Map<String, Object> errorBody = new LinkedHashMap<>();
        errorBody.put("status", HttpStatus.NOT_FOUND.value());
        errorBody.put("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        errorBody.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody);
    }

    // captures first @valid exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest req){
        String errorMessages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getDefaultMessage())
                .findFirst()
                .orElse("Bad Request");
        log.error(errorMessage, errorMessages, req.getRequestURL().toString());

        Map<String, Object> errorBody = new LinkedHashMap<>();
        errorBody.put("status", HttpStatus.BAD_REQUEST.value());
        errorBody.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        errorBody.put("message", errorMessages);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
    }

    // captures invalid enum exceptions
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleEnumError(HttpMessageNotReadableException ex, HttpServletRequest req) {
        String message = ex.getMessage();
        String extractedEnumValues = "Invalid enum value";
        Pattern pattern = Pattern.compile("accepted for Enum class: \\[([^\\]]+)]");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            extractedEnumValues = "Allowed values: " + matcher.group(1);
        }
        log.error(errorMessage, extractedEnumValues, req.getRequestURL().toString());

        Map<String, Object> errorBody = new LinkedHashMap<>();
        errorBody.put("status", HttpStatus.BAD_REQUEST.value());
        errorBody.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        errorBody.put("message", extractedEnumValues);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAuthorizationDeniedException(AuthorizationDeniedException ex, HttpServletRequest req) {
        log.error(errorMessage, ex.getMessage(), req.getRequestURL().toString());
        Map<String, Object> errorBody = new LinkedHashMap<>();
        errorBody.put("status", HttpStatus.FORBIDDEN.value());
        errorBody.put("error", HttpStatus.FORBIDDEN.getReasonPhrase());
        errorBody.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorBody);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(Exception ex, HttpServletRequest req) {
        log.error(errorMessage, ex.getMessage(), req.getRequestURL().toString());
        Map<String, Object> errorBody = new LinkedHashMap<>();
        errorBody.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorBody.put("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        errorBody.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
    }
}
