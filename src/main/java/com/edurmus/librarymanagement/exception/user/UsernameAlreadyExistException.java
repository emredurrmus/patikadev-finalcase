package com.edurmus.librarymanagement.exception.user;

public class UsernameAlreadyExistException extends RuntimeException    {

    public UsernameAlreadyExistException(String message) {
        super(message);
    }

    public UsernameAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public UsernameAlreadyExistException(Throwable cause) {
        super(cause);
    }

}
