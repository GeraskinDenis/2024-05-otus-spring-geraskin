package ru.otus.hw.exceptions;

import org.springframework.dao.DataIntegrityViolationException;

public class JdbcDataIntegrityViolationException extends DataIntegrityViolationException {

    public JdbcDataIntegrityViolationException(String msg) {
        super(msg);
    }

    public JdbcDataIntegrityViolationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
