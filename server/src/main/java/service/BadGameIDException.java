package service;

import dataaccess.DataAccessException;

public class BadGameIDException extends DataAccessException {
    public BadGameIDException(String message) {
        super(message);
    }
}
