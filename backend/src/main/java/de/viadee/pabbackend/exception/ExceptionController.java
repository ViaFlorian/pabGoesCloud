package de.viadee.pabbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ExceptionController {

  @ExceptionHandler({PabException.class})
  public ResponseEntity<ErrorResponse> handlePabException(Exception ex, WebRequest request) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse(ex.getMessage()));
  }

  @ExceptionHandler({PabValidatorException.class})
  public ResponseEntity<ErrorResponse> handlePabValidatorException(Exception ex,
      WebRequest request) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
        .body(new ErrorResponse(ex.getMessage()));
  }

  @ExceptionHandler({RuntimeException.class})
  public ResponseEntity<ErrorResponse> handleRuntimeException(Exception ex, WebRequest request) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse(ex.getMessage()));
  }

}
