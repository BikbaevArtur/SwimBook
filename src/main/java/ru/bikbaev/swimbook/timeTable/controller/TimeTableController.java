package ru.bikbaev.swimbook.timeTable.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bikbaev.swimbook.client.exception.ClientNotFoundException;
import ru.bikbaev.swimbook.common.exception.ErrorMessage;
import ru.bikbaev.swimbook.timeTable.dto.*;
import ru.bikbaev.swimbook.timeTable.exception.*;
import ru.bikbaev.swimbook.timeTable.service.TimeTableService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;


@Tag(name = "API для работы с записями")
@RestController
@RequestMapping("/api/v0/pool/timetable")
public class TimeTableController {
    private final TimeTableService timeTableService;

    public TimeTableController(TimeTableService timeTableService) {
        this.timeTableService = timeTableService;
    }

    @Operation(summary = "Получение занятых записей на определенную дату")
    @GetMapping("/all")
    public ResponseEntity<List<TimeRecord>>getAll(@RequestParam String date){
        LocalDate localDate = LocalDate.parse(date);
        return ResponseEntity.status(HttpStatus.OK).body(timeTableService.getAllPoolTimetables(localDate));
    }

    @Operation(summary = "Получение доступных записей на определенную дату")
    @GetMapping("/available")
    public ResponseEntity<List<TimeRecord>>getAvailable(@RequestParam String date){
        LocalDate localDate = LocalDate.parse(date);
        return ResponseEntity.status(HttpStatus.OK).body(timeTableService.getAvailableTimetablesForDate(localDate));
    }

    @Operation(summary = "Добавить запись клиента на определенное время")
    @PostMapping("/reserve")
    public ResponseEntity<UUID>reserve(@RequestBody @Valid ReserveRequest reserveRequest){
        return ResponseEntity.status(HttpStatus.OK).body(timeTableService.createNewReserve(reserveRequest));
    }

    @Operation(summary = "Добавить запись клиента на несколько часов подряд")
    @PostMapping("/reserve-multiple")
    public ResponseEntity<List<UUID>> reserveMultipleHours(@RequestBody @Valid MultiHourReservationRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(timeTableService.reserveMultipleHours(request));
    }

    @Operation(summary = "Отмена записи клиента на определенное время")
    @GetMapping("/cancel")
    public ResponseEntity<Void>cancel(@RequestBody @Valid CancelRequest cancelRequest){
        timeTableService.cancelReserve(cancelRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Поиск записей (по ФИО, дате посещения)")
    @GetMapping("/visits/search")
    public ResponseEntity<List<VisitDto>> searchVisits(@RequestBody @Valid PoolVisitSearchRequest request){
        return  ResponseEntity.status(HttpStatus.OK).body(timeTableService.searchVisitsByNameAndDate(request));
    }




    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ErrorMessage> handlerDateTimeParseException(){
        ErrorMessage message = new ErrorMessage("Не корректный формат даты, введите yyyy-MM-dd (например 2025-04-21) ",HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    @ExceptionHandler(InvalidTimeSlotException.class)
    public ResponseEntity<ErrorMessage> handlerInvalidTimeSlotException(InvalidTimeSlotException exception){
        ErrorMessage errorMessage = new ErrorMessage(exception.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(NoAvailableSlotsException.class)
    public ResponseEntity<ErrorMessage> handlerNoAvailableSlotsException(NoAvailableSlotsException exception){
        ErrorMessage message = new ErrorMessage(exception.getMessage(),HttpStatus.CONFLICT);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
    }

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<ErrorMessage> handlerClientNotFoundException(ClientNotFoundException exception){
        ErrorMessage message = new ErrorMessage(exception.getMessage(),HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }

    @ExceptionHandler(TimeTableNotFoundException.class)
    public ResponseEntity<ErrorMessage> handlerTimeTableNotFoundException(TimeTableNotFoundException exception){
        ErrorMessage message = new ErrorMessage(exception.getMessage(),HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }

    @ExceptionHandler(ClientMismatchException.class)
    public ResponseEntity<ErrorMessage> handlerClientMismatchException(ClientMismatchException exception){
        ErrorMessage message = new ErrorMessage(exception.getMessage(),HttpStatus.FORBIDDEN);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
    }

    @ExceptionHandler(PoolClosedException.class)
    public ResponseEntity<ErrorMessage> handlerPoolClosedException(PoolClosedException exception){
        ErrorMessage message = new ErrorMessage(exception.getMessage(),HttpStatus.CONFLICT);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
    }


}
