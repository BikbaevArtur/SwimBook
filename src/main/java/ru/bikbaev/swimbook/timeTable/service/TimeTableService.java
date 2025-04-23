package ru.bikbaev.swimbook.timeTable.service;

import org.springframework.stereotype.Service;
import ru.bikbaev.swimbook.client.model.Client;
import ru.bikbaev.swimbook.client.service.ClientService;
import ru.bikbaev.swimbook.schedule.service.ScheduleService;
import ru.bikbaev.swimbook.timeTable.dto.*;
import ru.bikbaev.swimbook.timeTable.exception.*;
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

    /**
     * TODO: мб стоит вывести в бд для гибкости
     */
    private final int MAX_COUNT_RECORD = 10;

    public TimeTableService(JpaTimeTable repository,
                            ScheduleService scheduleService, ClientService clientService) {
        this.repository = repository;
        this.scheduleService = scheduleService;
        this.clientService = clientService;

    }

    /**
     * Возвращает список занятых временных интервалов для указанной даты
     * с указыванием кол-во занятых мест на каждый интервал времени
     *
     * @param localDate дата записи
     * @return список  {@link TimeRecord} объекта содержащий время записи, и кол-во записей
     */
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

    /**
     * Возвращает список доступных временных интервалов для указанной даты
     * с указанием кол-во оставшихся свободных мест на каждый интервал.
     *
     * @param localDate дата записи
     * @return список {@link TimeRecord} объекта содержащий время записи, и кол во свободных мест
     */
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

    /**
     * Создает новую запись на определенную дату и время в расписании бассейна.
     * Перед созданием записи метод проверяет:
     * что время указано точно по часу (например, 14:00), иначе выбрасывается {@link InvalidTimeSlotException}
     * что на выбранный слот еще есть свободные места, иначе выбрасывается {@link NoAvailableSlotsException}
     *
     * @param reserveRequest id клиента и дата/время записи
     * @return UUID созданной записи
     */
    public OrderIdResponse createNewReserve(ReserveRequest reserveRequest) {

        LocalDate date = reserveRequest.getDatetime().toLocalDate();
        LocalTime time = reserveRequest.getDatetime().toLocalTime();

        checkInvalidTimeSlotException(time);

        checkPoolClosedException(date, time);

        Map<LocalTime, List<UUID>> map = getTimeRecord(date);

        checkNoAvailableSlotsException(time, map);

        Client client = clientService.getClientEntityById(reserveRequest.getClientId());

        TimeTable result = repository.save(
                TimeTable
                        .builder()
                        .client(client)
                        .date(date)
                        .time(time)
                        .build());

        return new OrderIdResponse(result.getOrderId().toString());
    }


    /**
     * Создает несколько записей для одного клиента на указанный временной интервал.
     * Проверка:
     * Начало и окончания записи кратно часу
     * Указанный интервал попадает в рабочее время
     * Свободные места на каждый часовой диапазон
     *
     * @param request {@link MultiHourReservationRequest}
     *                clietnId - id клиента
     *                date - дата записи
     *                startTime - время записи
     *                durationHours - длительность в часах
     * @return список {@link UUID} — id записей
     */
    public List<OrderIdResponse> reserveMultipleHours(MultiHourReservationRequest request) {
        LocalDate date = request.getDate();
        LocalTime startTime = request.getStartTime();
        LocalTime endTime = startTime.plusHours(request.getDurationHours());

        checkInvalidTimeSlotException(startTime);

        checkPoolClosedException(date, startTime, endTime);

        Map<LocalTime, List<UUID>> map = getTimeRecord(date);

        Client client = clientService.getClientEntityById(request.getClientId());

        List<TimeTable> timeTables = new ArrayList<>();

        for (LocalTime start = startTime; start.isBefore(endTime); start = start.plusHours(1)) {
            checkNoAvailableSlotsException(start, map);
            timeTables.add(
                    TimeTable.builder().client(client).date(date).time(start).build()
            );
        }

        return repository.saveAll(timeTables)
                .stream()
                .map(e->new OrderIdResponse(e.getOrderId().toString()))
                .toList();
    }


    /**
     * Отменяет созданную запись
     * Перед отменой, проверяет:
     * существование клиента по переданному id
     * существование записи по переданному id
     * проверяет, что клиент является владельцем записи
     *
     * @param cancelRequest id пользователя и id записи
     */
    public void cancelReserve(CancelRequest cancelRequest) {
        UUID orderId = cancelRequest.getOrderId();

        Client client = clientService.getClientEntityById(cancelRequest.getClientId());

        TimeTable timeTable = repository.findById(cancelRequest.getOrderId())
                .orElseThrow(() -> new TimeTableNotFoundException("Запись с id: " + orderId + " отсутсвтует"));

        if (!Objects.equals(client.getId(), timeTable.getClient().getId())) {
            throw new ClientMismatchException(
                    String.format("Клиент с id %d не соответствует владельцу записи с id %s", client.getId(), orderId)
            );
        }

        repository.deleteById(orderId);
    }


    /**
     * Ищет все записи  клиента по его имени и дате.
     *
     * @param request {@link PoolVisitSearchRequest} ФИО клиента и дата
     * @return список визитов клиента {@link VisitDto} id записи и время
     */
    public List<VisitDto> searchVisitsByNameAndDate(PoolVisitSearchRequest request) {

        Client client = clientService.findByName(request.getName());

        List<TimeTable> reserveListClient = repository
                .findByClientIdAndDateOrderByTime(client.getId(), request.getDate());

        return reserveListClient
                .stream()
                .map(e -> new VisitDto(e.getOrderId(), e.getTime()))
                .toList();
    }

    /**
     * Получение время записи и список id записей на указанное время
     * ключ {@link LocalTime } - время записи
     * значение {@link UUID} - список id записей
     *
     * @param date дата записи
     * @return возвращяет Map с временем записи, и списком id записей
     */
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

    private void checkInvalidTimeSlotException(LocalTime time) {
        if (time.getMinute() != 0 || time.getSecond() != 0 || time.getNano() != 0) {
            throw new InvalidTimeSlotException("Время должно быть кратно часу(например 10:00)");
        }
    }

    private void checkNoAvailableSlotsException(LocalTime time, Map<LocalTime, List<UUID>> map) {
        if (map.containsKey(time) && MAX_COUNT_RECORD <= map.get(time).size()) {
            throw new NoAvailableSlotsException("На данное время " + time + " нет свободных мест");
        }
    }

    private void checkPoolClosedException(LocalDate date, LocalTime time) {
        if (!scheduleService.validatePoolWorkingHours(date, time)) {
            throw new PoolClosedException(String.format("бассейн закрыт %s %s",date,time));
        }
    }

    private void checkPoolClosedException(LocalDate date, LocalTime startTime, LocalTime endTime) {
        if (!scheduleService.validatePoolWorkingInterval(date, startTime, endTime)) {
            throw new PoolClosedException(String.format("%s интервал %s - %s приходится на нерабочее время бассейна", date, startTime, endTime));
        }
    }

}
