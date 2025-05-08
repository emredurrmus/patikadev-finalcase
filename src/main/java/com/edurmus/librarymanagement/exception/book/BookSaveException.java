package com.edurmus.librarymanagement.exception.book;

public class BookSaveException extends RuntimeException{


    public BookSaveException(String message) {
        super(message);
    }


    public BookSaveException(String message, Throwable cause) {
        super(message, cause);
    }


    public BookSaveException(Throwable cause) {
        super(cause);
    }
}
