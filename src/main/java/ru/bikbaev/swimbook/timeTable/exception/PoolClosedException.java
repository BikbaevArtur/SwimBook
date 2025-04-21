package ru.bikbaev.swimbook.timeTable.exception;

public class PoolClosedException extends  RuntimeException{
    public PoolClosedException(String message) {
        super(message);
    }

    public PoolClosedException(String message, Throwable cause) {
        super(message, cause);
    }
}
