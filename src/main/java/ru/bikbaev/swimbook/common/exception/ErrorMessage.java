package ru.bikbaev.swimbook.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ErrorMessage {
    private String message;
    private HttpStatus httpStatus;
}
