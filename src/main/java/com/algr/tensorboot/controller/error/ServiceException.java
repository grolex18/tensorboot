package com.algr.tensorboot.controller.error;

/**
 * General service exception class.
 */
public class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }
}
