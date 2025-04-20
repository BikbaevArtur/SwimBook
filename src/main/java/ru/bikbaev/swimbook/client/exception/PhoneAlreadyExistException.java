package ru.bikbaev.swimbook.client.exception;

public class PhoneAlreadyExistException extends RuntimeException{

    public PhoneAlreadyExistException(String message) {
        super(message);
    }

    public PhoneAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
