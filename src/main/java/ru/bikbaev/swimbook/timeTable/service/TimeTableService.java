package ru.bikbaev.swimbook.timeTable.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.bikbaev.swimbook.client.model.Client;
import ru.bikbaev.swimbook.client.service.ClientService;
import ru.bikbaev.swimbook.schedule.service.ScheduleService;
import ru.bikbaev.swimbook.timeTable.dto.CancelRequest;
import ru.bikbaev.swimbook.timeTable.dto.ReserveRequest;
import ru.bikbaev.swimbook.timeTable.dto.TimeRecord;
import ru.bikbaev.swimbook.timeTable.exception.ClientMismatchException;
import ru.bikbaev.swimbook.timeTable.exception.InvalidTimeSlotException;
import ru.bikbaev.swimbook.timeTable.exception.NoAvailableSlotsException;
import ru.bikbaev.swimbook.timeTable.exception.TimeTableNotFoundException;
import ru.bikbaev.swimbook.timeTable.model.TimeTable;
import ru.bikbaev.swimbook.timeTable.repository.JpaTimeTable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class TimeTableService {
    private final JpaTimeTable repository;
    private final ScheduleService scheduleService;
    private final ClientService clientService;
    private final int MAX_COUNT_RECORD;

    public TimeTableService(JpaTimeTable repository,
                            ScheduleService scheduleService, ClientService clientService,
                            @Value("${timetable.max-record-count}") int maxCountRecord) {
        this.repository = repository;
        this.scheduleService = scheduleService;
        this.clientService = clientService;
        this.MAX_COUNT_RECORD = maxCountRecord;

    }

    public List<TimeRecord> getAllPoolTimetables(LocalDate localDate) {
        List<TimeRecord> timeRecords = scheduleService.createTimeRecordsForSchedule(localDate);

        if (timeRecords.isEmpty()) {
            return Collections.emptyList();
        }

        Map<LocalTime, List<UUID>> map = getTimeRecord(localDate);
        if (map.isEmpty()) {
            return timeRecords;
        }

        for (TimeRecord t : timeRecords) {
            if (map.containsKey(t.getTime())) {
                t.setCount(map.get(t.getTime()).size());
            }
        }
        return timeRecords;
    }

    public List<TimeRecord> getAvailableTimetablesForDate(LocalDate localDate) {
        List<TimeRecord> timeRecords = scheduleService.createTimeRecordsForSchedule(localDate);

        if (timeRecords.isEmpty()) {
            return Collections.emptyList();
        }

        Map<LocalTime, List<UUID>> map = getTimeRecord(localDate);
        if (map.isEmpty()) {
            return timeRecords;
        }

        for (TimeRecord t : timeRecords) {
            if (map.containsKey(t.getTime())) {
                int count = map.get(t.getTime()).size();
                t.setCount(MAX_COUNT_RECORD - count);
            } else t.setCount(MAX_COUNT_RECORD);
        }
        return timeRecords;
    }

    public UUID createNewReserve(ReserveRequest reserveRequest) {

        LocalDate date = reserveRequest.getDatetime().toLocalDate();
        LocalTime time = reserveRequest.getDatetime().toLocalTime();

        if (time.getMinute() != 0 || time.getSecond() != 0 || time.getNano() != 0) {
            throw new InvalidTimeSlotException("Время должно быть кратно часу(например 10:00)");
        }

        Map<LocalTime, List<UUID>> map = getTimeRecord(date);

        if (map.containsKey(time) && MAX_COUNT_RECORD <= map.get(time).size()) {
            throw new NoAvailableSlotsException("На данное время " + time + " все записи заняты");
        }

        Client client = clientService.getClientEntityById(reserveRequest.getClientId());


        return repository.save(TimeTable
                        .builder()
                        .client(client)
                        .date(date)
                        .time(time)
                        .build())
                .getOrderId();
    }


    public void cancelReserve(CancelRequest cancelRequest) {
        UUID orderId = cancelRequest.getOrderId();

        Client client = clientService.getClientEntityById(cancelRequest.getClientId());

        TimeTable timeTable = repository.findById(cancelRequest.getOrderId())
                .orElseThrow(()->new TimeTableNotFoundException("Запись с id: "+orderId + " отсутсвтует"));

        if(!Objects.equals(client.getId(), timeTable.getClient().getId())){
            throw new ClientMismatchException(
                    String.format("Клиент с id %d не соответствует владельцу записи с id %s", client.getId(), orderId)
            );
        }

        repository.deleteById(orderId);
    }

    private Map<LocalTime, List<UUID>> getTimeRecord(LocalDate date) {
        Map<LocalTime, List<UUID>> recordDay = new HashMap<>();

        List<TimeTable> timeTables = repository.findByDateOrderByTime(date);
        if (timeTables.isEmpty()) {
            return Collections.emptyMap();
        }
        for (TimeTable r : timeTables) {
            recordDay.computeIfAbsent(r.getTime(), k -> new ArrayList<>()).add(r.getOrderId());
        }
        return recordDay;
    }
}
