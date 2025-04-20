package ru.bikbaev.swimbook.client.exception;

public class ClientAlreadyExistException extends RuntimeException{
    public ClientAlreadyExistException(String message) {
        super(message);
    }

    public ClientAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
