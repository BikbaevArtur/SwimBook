package ru.bikbaev.swimbook.timeTable.exception;

public class NoAvailableSlotsException extends  RuntimeException{
    public NoAvailableSlotsException(String message) {
        super(message);
    }

    public NoAvailableSlotsException(String message, Throwable cause) {
        super(message, cause);
    }
}
