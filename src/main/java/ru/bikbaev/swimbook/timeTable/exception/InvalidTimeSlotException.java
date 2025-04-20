package ru.bikbaev.swimbook.timeTable.exception;

public class InvalidTimeSlotException extends RuntimeException{
    public InvalidTimeSlotException(String message) {
        super(message);
    }

    public InvalidTimeSlotException(String message, Throwable cause) {
        super(message, cause);
    }
}
