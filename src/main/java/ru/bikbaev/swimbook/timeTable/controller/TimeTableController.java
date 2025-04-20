package ru.bikbaev.swimbook.timeTable.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bikbaev.swimbook.timeTable.dto.CancelRequest;
import ru.bikbaev.swimbook.timeTable.dto.TimeRecord;
import ru.bikbaev.swimbook.timeTable.dto.ReserveRequest;
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

    @GetMapping("/cancel")
    public ResponseEntity<Void>cancel(@RequestBody CancelRequest cancelRequest){
        timeTableService.cancelReserve(cancelRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
