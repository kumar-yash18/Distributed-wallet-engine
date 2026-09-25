package com.yash.walletengine.exception;

import com.yash.walletengine.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWalletNotFound(WalletNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("WALLET_NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Catch-all fallback — explained in Step 6
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", "Something went wrong");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}