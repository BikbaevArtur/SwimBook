package ru.bikbaev.swimbook.timeTable.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bikbaev.swimbook.client.exception.ClientNotFoundException;
import ru.bikbaev.swimbook.common.exception.ErrorMessage;
import ru.bikbaev.swimbook.timeTable.dto.*;
import ru.bikbaev.swimbook.timeTable.exception.ClientMismatchException;
import ru.bikbaev.swimbook.timeTable.exception.InvalidTimeSlotException;
import ru.bikbaev.swimbook.timeTable.exception.NoAvailableSlotsException;
import ru.bikbaev.swimbook.timeTable.exception.TimeTableNotFoundException;
import ru.bikbaev.swimbook.timeTable.service.TimeTableService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v0/pool/timetable")
public class TimeTableController {
    private final TimeTableService timeTableService;

    public TimeTableController(TimeTableService timeTableService) {
        this.timeTableService = timeTableService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<TimeRecord>>getAll(@RequestParam String date){
        LocalDate localDate = LocalDate.parse(date);
        return ResponseEntity.status(HttpStatus.OK).body(timeTableService.getAllPoolTimetables(localDate));
    }


    @GetMapping("/available")
    public ResponseEntity<List<TimeRecord>>getAvailable(@RequestParam String date){
        LocalDate localDate = LocalDate.parse(date);
        return ResponseEntity.status(HttpStatus.OK).body(timeTableService.getAvailableTimetablesForDate(localDate));
    }


    @PostMapping("/reserve")
    public ResponseEntity<UUID>reserve(@RequestBody ReserveRequest reserveRequest){
        return ResponseEntity.status(HttpStatus.OK).body(timeTableService.createNewReserve(reserveRequest));
    }

    @RequestMapping("/reserve-multiple")
    public ResponseEntity<List<UUID>> reserveMultipleHours(@RequestBody MultiHourReservationRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(timeTableService.reserveMultipleHours(request));
    }

    @GetMapping("/cancel")
    public ResponseEntity<Void>cancel(@RequestBody CancelRequest cancelRequest){
        timeTableService.cancelReserve(cancelRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/visits/search")
    public ResponseEntity<List<VisitDto>> searchVisits(@RequestBody PoolVisitSearchRequest request){
        return  ResponseEntity.status(HttpStatus.OK).body(timeTableService.searchVisitsByNameAndDate(request));
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


}
