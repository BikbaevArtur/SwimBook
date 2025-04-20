package ru.bikbaev.swimbook.timeTable.exception;

public class ClientMismatchException extends RuntimeException{
    public ClientMismatchException(String message) {
        super(message);
    }

    public ClientMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
