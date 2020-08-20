package com.sapient.footballleague.exception;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.sapient.footballleague.pojo.CustomErrorResponse;

/**
 * Class to handle custom exception.
 * @author suppraka
 *
 */
@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles NoDataFoundException Exception.
     * @param ex exception object.
     * @param request
     * @return Response Entity when data is not found.
     * @throws IOException
     */
    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<CustomErrorResponse>  springHandleNotFound(Exception ex) throws IOException {

        CustomErrorResponse errors = new CustomErrorResponse();
        errors.setError(ex.getMessage());
        errors.setStatus(HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }
    
    /**
     * @param ex exception object.
     * @param request request object.
     * @return Response entity for generic exception.
     * @throws IOException
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse>  springHandleException(Exception ex) throws IOException {

        CustomErrorResponse errors = new CustomErrorResponse();
        errors.setError(ex.getMessage());
        errors.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}