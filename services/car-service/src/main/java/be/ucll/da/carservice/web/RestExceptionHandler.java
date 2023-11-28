package be.ucll.da.carservice.web;

import be.ucll.da.carservice.api.model.ApiError;
import be.ucll.da.carservice.domain.NoOwnerException;
import be.ucll.da.carservice.domain.NoTypeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({NoTypeException.class})
    public ResponseEntity<ApiError> handleNoTypeException() {
        ApiError error = new ApiError();
        error.setCode("12");
        error.setMessage("Type should not be empty");

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler({NoOwnerException.class})
    public ResponseEntity<ApiError> handleNoOwnerException() {
        ApiError error = new ApiError();
        error.setCode("12");
        error.setMessage("Onwer should not be empty");

        return ResponseEntity.badRequest().body(error);
    }
}
