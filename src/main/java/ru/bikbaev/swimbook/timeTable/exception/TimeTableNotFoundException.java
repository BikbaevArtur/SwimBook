package ru.bikbaev.swimbook.timeTable.exception;

public class TimeTableNotFoundException extends RuntimeException{
    public TimeTableNotFoundException(String message) {
        super(message);
    }

    public TimeTableNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
