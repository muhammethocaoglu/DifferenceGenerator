package com.waes.app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when BinaryData not found in repository.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class BinaryDataNotFoundInRepoException extends RuntimeException
{
    private static final long serialVersionUID = 4963294382733974718L;

    public BinaryDataNotFoundInRepoException(String message)
    {
        super(message);
    }

    public BinaryDataNotFoundInRepoException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
