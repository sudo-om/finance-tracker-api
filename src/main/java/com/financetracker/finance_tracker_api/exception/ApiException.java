package com.financetracker.finance_tracker_api.exception;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
