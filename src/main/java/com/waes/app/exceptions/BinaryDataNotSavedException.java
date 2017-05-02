package com.waes.app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * BinaryDataNotSavedException is thrown when BinaryData cannot be saved to repository due to a failure in repository.
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class BinaryDataNotSavedException extends RuntimeException
{
    private static final long serialVersionUID = 5796360641758836097L;

    public BinaryDataNotSavedException(String message)
    {
        super(message);
    }

    public BinaryDataNotSavedException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
