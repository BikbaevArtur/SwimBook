package ru.bikbaev.swimbook.timeTable.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bikbaev.swimbook.client.exception.ClientNotFoundException;
import ru.bikbaev.swimbook.client.model.Client;
import ru.bikbaev.swimbook.client.service.ClientService;
import ru.bikbaev.swimbook.schedule.service.ScheduleService;
import ru.bikbaev.swimbook.timeTable.dto.*;
import ru.bikbaev.swimbook.timeTable.exception.*;
import ru.bikbaev.swimbook.timeTable.model.TimeTable;
import ru.bikbaev.swimbook.timeTable.repository.JpaTimeTable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TimeTableServiceTest {

    @Mock
    private JpaTimeTable repository;
    @Mock
    private ScheduleService scheduleService;
    @Mock
    private ClientService clientService;

    @InjectMocks
    private TimeTableService service;


    private List<TimeRecord> createTimeRecord() {
        List<TimeRecord> recordList = new ArrayList<>();

        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(14, 0);

        while (startTime.isBefore(endTime)) {
            recordList.add(new TimeRecord(startTime, 0));
            startTime = startTime.plusHours(1);
        }

        return recordList;
    }

    private List<TimeTable> createTimeTable() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();

        return Arrays.asList(
                TimeTable.builder()
                        .client(createClient())
                        .time(LocalTime.of(10, 0)).orderId(id1).build(),
                TimeTable.builder()
                        .client(createClient())
                        .time(LocalTime.of(10, 0)).orderId(id2).build(),
                TimeTable.builder()
                        .client(createClient())
                        .time(LocalTime.of(13, 0)).orderId(id3).build()
        );
    }

    private Client createClient() {
        return Client.builder()
                .id(1L)
                .name("Ivan")
                .phone("+77777777777")
                .email("ivan@mail.ru")
                .build();
    }


    //1 ----------------------------------------
    @Test
    void getAllPoolTimetablesTest() {
        LocalDate date = LocalDate.of(2025, 4, 21);

        List<TimeRecord> timeRecords = createTimeRecord();

        List<TimeTable> timeTableList = createTimeTable();

        when(scheduleService.createTimeRecordsForSchedule(date)).thenReturn(timeRecords);
        when(repository.findByDateOrderByTime(date)).thenReturn(timeTableList);

        List<TimeRecord> result = service.getAllPoolTimetables(date);

        assertNotNull(result);

        assertEquals(result.get(0).getTime(), LocalTime.of(9, 0));
        assertEquals(result.get(0).getCount(), 0);

        assertEquals(result.get(1).getTime(), LocalTime.of(10, 0));
        assertEquals(result.get(1).getCount(), 2);

        assertEquals(result.get(2).getTime(), LocalTime.of(11, 0));
        assertEquals(result.get(2).getCount(), 0);

        assertEquals(result.get(3).getTime(), LocalTime.of(12, 0));
        assertEquals(result.get(3).getCount(), 0);

        assertEquals(result.get(4).getTime(), LocalTime.of(13, 0));
        assertEquals(result.get(4).getCount(), 1);

        verify(scheduleService, times(1)).createTimeRecordsForSchedule(date);
        verify(repository, times(1)).findByDateOrderByTime(date);
    }

    //2 ----------------------------------------
    @Test
    void getAvailableTimetablesForDateTest() {
        LocalDate date = LocalDate.of(2025, 4, 21);

        List<TimeRecord> timeRecords = createTimeRecord();

        List<TimeTable> timeTableList = createTimeTable();

        when(scheduleService.createTimeRecordsForSchedule(date)).thenReturn(timeRecords);
        when(repository.findByDateOrderByTime(date)).thenReturn(timeTableList);

        List<TimeRecord> result = service.getAvailableTimetablesForDate(date);

        assertNotNull(result);

        assertEquals(result.get(0).getTime(), LocalTime.of(9, 0));
        assertEquals(result.get(0).getCount(), 10);

        assertEquals(result.get(1).getTime(), LocalTime.of(10, 0));
        assertEquals(result.get(1).getCount(), 8);

        assertEquals(result.get(2).getTime(), LocalTime.of(11, 0));
        assertEquals(result.get(2).getCount(), 10);

        assertEquals(result.get(3).getTime(), LocalTime.of(12, 0));
        assertEquals(result.get(3).getCount(), 10);

        assertEquals(result.get(4).getTime(), LocalTime.of(13, 0));
        assertEquals(result.get(4).getCount(), 9);

        verify(scheduleService, times(1)).createTimeRecordsForSchedule(date);
        verify(repository, times(1)).findByDateOrderByTime(date);
    }

    //3 ----------------------------------------
    @Test
    void createNewReserveTest() {
        Long userId = 1L;
        LocalDate date = LocalDate.of(2025, 4, 21);
        LocalTime time = LocalTime.of(10, 0);

        ReserveRequest reserveRequest = new ReserveRequest(1L, LocalDateTime.of(date, time));

        List<TimeTable> timeTables = createTimeTable();
        Client client = createClient();

        UUID uuid = UUID.randomUUID();

        when(repository.findByDateOrderByTime(date)).thenReturn(timeTables);
        when(scheduleService.validatePoolWorkingHours(date,time)).thenReturn(true);
        when(clientService.getClientEntityById(userId)).thenReturn(client);
        when(repository.save(any(TimeTable.class))).thenAnswer(
                e -> {
                    TimeTable tt = e.getArgument(0);
                    tt.setOrderId(uuid);
                    return tt;
                }
        );

        OrderIdResponse result = service.createNewReserve(reserveRequest);

        assertNotNull(result);
        assertEquals(result.getOrderId(), uuid.toString());

        verify(repository, times(1)).findByDateOrderByTime(date);
        verify(scheduleService,times(1)).validatePoolWorkingHours(date,time);
        verify(clientService, times(1)).getClientEntityById(userId);
        verify(repository, times(1)).save(any(TimeTable.class));
    }

    @Test
    void createNewReserveThrowInvalidTimeSlotExceptionTest() {
        Long userId = 1L;
        LocalDate date = LocalDate.of(2025, 4, 21);
        LocalTime time1 = LocalTime.of(10, 30);
        LocalTime time2 = LocalTime.of(10, 0, 1);
        LocalTime time3 = LocalTime.of(10, 0, 0, 1);


        assertThrows(InvalidTimeSlotException.class, () -> service.createNewReserve(
                new ReserveRequest(userId, LocalDateTime.of(date, time1))
        ));
        assertThrows(InvalidTimeSlotException.class, () -> service.createNewReserve(
                new ReserveRequest(userId, LocalDateTime.of(date, time2))
        ));
        assertThrows(InvalidTimeSlotException.class, () -> service.createNewReserve(
                new ReserveRequest(userId, LocalDateTime.of(date, time3))
        ));

        verify(repository, never()).save(any());
    }

    @Test
    void createNewReserveThrowPoolClosedExceptionTest() {
        LocalDate date = LocalDate.of(2025, 4, 21);
        Long clientId = 1L;

        LocalTime time = LocalTime.of(8, 0);

        ReserveRequest request = new ReserveRequest(clientId, LocalDateTime.of(date, time));

        when(scheduleService.validatePoolWorkingHours(date, time)).thenReturn(false);

        assertThrows(PoolClosedException.class, () -> service.createNewReserve(request));

        verify(scheduleService, times(1)).validatePoolWorkingHours(date, time);
        verify(repository, never()).save(any());
    }

    @Test
    void createNewReserveThrowNoAvailableSlotsExceptionTest() {
        Long clientId = 1L;
        LocalDateTime dateTime = LocalDateTime.of(2025, 4, 22, 10, 0);

        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();

        ReserveRequest request = new ReserveRequest(clientId, dateTime);

        List<TimeTable> fullList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            fullList.add(TimeTable.builder()
                    .orderId(UUID.randomUUID())
                    .time(LocalTime.of(10, 0))
                    .date(LocalDate.of(2025, 4, 22))
                    .client(createClient())
                    .build());
        }

        when(scheduleService.validatePoolWorkingHours(date,time)).thenReturn(true);

        when(repository.findByDateOrderByTime(dateTime.toLocalDate()))
                .thenReturn(fullList);

        assertThrows(NoAvailableSlotsException.class, () ->
                service.createNewReserve(request));
        verify(repository, never()).save(any());
    }

    @Test
    void createNewReserveThrowClientNotFoundExceptionTest() {
        Long clientId = 1L;
        LocalDateTime dateTime = LocalDateTime.of(2025, 4, 22, 10, 0);

        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();

        ReserveRequest request = new ReserveRequest(clientId, dateTime);

        when(scheduleService.validatePoolWorkingHours(date,time)).thenReturn(true);
        when(clientService.getClientEntityById(clientId))
                .thenThrow(new ClientNotFoundException("Клиент не найден"));

        assertThrows(ClientNotFoundException.class, () ->
                service.createNewReserve(request));

        verify(repository, never()).save(any());
    }

    //4 ----------------------------------------
    @Test
    void reserveMultipleHoursTest(){
        Long clientId = 1L;
        LocalDate date = LocalDate.of(2025,4,21);
        int duration  = 3;

        LocalTime startTime = LocalTime.of(12,0);
        LocalTime endTime = startTime.plusHours(duration);

        MultiHourReservationRequest request =MultiHourReservationRequest
                .builder()
                .clientId(clientId)
                .date(date)
                .startTime(startTime)
                .durationHours(duration)
                .build();

        Client client = createClient();

        when(scheduleService.validatePoolWorkingInterval(date,startTime,endTime)).thenReturn(true);
        when(repository.findByDateOrderByTime(date)).thenReturn(Collections.emptyList());
        when(clientService.getClientEntityById(clientId)).thenReturn(client);

        List<TimeTable> timeTables = new ArrayList<>();

        for (LocalTime start = startTime; start.isBefore(endTime); start = start.plusHours(1)) {
            timeTables.add(
                    TimeTable.builder().orderId(UUID.randomUUID()).client(client).date(date).time(start).build()
            );
        }

        when(repository.saveAll(any())).thenReturn(timeTables);

        List<OrderIdResponse> result = service.reserveMultipleHours(request);

        assertNotNull(result);

        assertEquals(result.size(),duration);

        verify(repository,times(1)).saveAll(any());
    }

    @Test
    void reserveMultipleHoursThrowCheckInvalidTimeSlotExceptionTest(){
        Long clientId = 1L;
        LocalDate date = LocalDate.of(2025,4,21);
        int duration  = 3;
        LocalTime startTime = LocalTime.of(12,30);

        MultiHourReservationRequest request =MultiHourReservationRequest
                .builder()
                .clientId(clientId)
                .date(date)
                .startTime(startTime)
                .durationHours(duration)
                .build();

        assertThrows(InvalidTimeSlotException.class,()->
              service.reserveMultipleHours(request));
    }

    @Test
    void reserveMultipleHoursThrowCheckPoolClosedExceptionTest(){
        Long clientId = 1L;
        LocalDate date = LocalDate.of(2025,4,21);
        int duration  = 3;
        LocalTime startTime = LocalTime.of(12,0);

        MultiHourReservationRequest request =MultiHourReservationRequest
                .builder()
                .clientId(clientId)
                .date(date)
                .startTime(startTime)
                .durationHours(duration)
                .build();
        assertThrows(PoolClosedException.class,()->
                service.reserveMultipleHours(request));
    }

    @Test
    void reserveMultipleHoursThrowClientNotFoundExceptionTest(){
        Long clientId = 1L;
        LocalDate date = LocalDate.of(2025,4,21);
        int duration  = 3;
        LocalTime startTime = LocalTime.of(12,0);
        LocalTime endTime = startTime.plusHours(duration);

        MultiHourReservationRequest request =MultiHourReservationRequest
                .builder()
                .clientId(clientId)
                .date(date)
                .startTime(startTime)
                .durationHours(duration)
                .build();
        when(scheduleService.validatePoolWorkingInterval(date,startTime,endTime)).thenReturn(true);
        when(clientService.getClientEntityById(clientId)).thenThrow(ClientNotFoundException.class);

        assertThrows(ClientNotFoundException.class,()->service.reserveMultipleHours(request));
    }

    @Test
    void reserveMultipleHoursThrowNoAvailableSlotsExceptionTest(){

        Long clientId = 1L;
        LocalDate date = LocalDate.of(2025,4,21);
        int duration  = 3;
        LocalTime startTime = LocalTime.of(12,0);
        LocalTime endTime = startTime.plusHours(duration);

        MultiHourReservationRequest request =MultiHourReservationRequest
                .builder()
                .clientId(clientId)
                .date(date)
                .startTime(startTime)
                .durationHours(duration)
                .build();
        Client client = createClient();

        List<TimeTable> timeTables = new ArrayList<>();
        for(int i=0;i<=10;i++){
            timeTables.add(
                    TimeTable
                            .builder()
                            .orderId(UUID.randomUUID())
                            .client(client)
                            .date(date)
                            .time(startTime)
                            .build()
            );
        }

        when(scheduleService.validatePoolWorkingInterval(date,startTime,endTime)).thenReturn(true);
        when(repository.findByDateOrderByTime(date)).thenReturn(timeTables);
        when(clientService.getClientEntityById(clientId)).thenReturn(client);

        assertThrows(NoAvailableSlotsException.class,
                ()->service.reserveMultipleHours(request));

    }

    //5 ----------------------------------------
    @Test
    void cancelReserveTest() {
        UUID orderId = UUID.randomUUID();
        Long userId = 1L;
        LocalDate date = LocalDate.of(2025, 4, 21);
        LocalTime time = LocalTime.of(10, 1);
        CancelRequest cancelRequest = new CancelRequest(userId, orderId);

        Client client = createClient();

        TimeTable timeTable = TimeTable.builder()
                .orderId(orderId)
                .client(client).date(date).time(time).build();

        when(clientService.getClientEntityById(userId)).thenReturn(client);
        when(repository.findById(orderId)).thenReturn(Optional.of(timeTable));

        service.cancelReserve(cancelRequest);

        verify(clientService, times(1)).getClientEntityById(userId);
        verify(repository, times(1)).findById(orderId);
        verify(repository, times(1)).deleteById(orderId);

    }

    @Test
    void cancelReserveThrowClientNotFoundExceptionTest() {
        UUID orderId = UUID.randomUUID();
        Long userId = 1L;
        CancelRequest cancelRequest = new CancelRequest(userId, orderId);

        when(clientService.getClientEntityById(userId)).thenThrow(
                new ClientNotFoundException("Клиент не найден")
        );

        assertThrows(ClientNotFoundException.class, () ->
                service.cancelReserve(cancelRequest));
        verify(repository, never()).findById(any());
        verify(repository, never()).deleteById(orderId);
    }

    @Test
    void cancelReserveThrowTimeTableNotFoundExceptionTest() {
        UUID orderId = UUID.randomUUID();
        Long userId = 1L;
        Client client = createClient();
        CancelRequest cancelRequest = new CancelRequest(userId, orderId);


        when(clientService.getClientEntityById(userId)).thenReturn(client);
        when(repository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(TimeTableNotFoundException.class, () -> service.cancelReserve(cancelRequest));

        verify(clientService, times(1)).getClientEntityById(userId);
        verify(repository, times(1)).findById(orderId);
        verify(repository, never()).deleteById(orderId);
    }

    @Test
    void cancelReserveThrowClientMismatchExceptionTest() {
        UUID orderId = UUID.randomUUID();
        Long userRequestId = 1L;

        Client clientRequest = createClient();

        CancelRequest cancelRequest = new CancelRequest(userRequestId, orderId);

        Client clientTable = Client.builder()
                .id(10L).name("Vadim").phone("+78888888888").email("vadim@mail.ru").build();

        LocalDate date = LocalDate.of(2025, 4, 21);
        LocalTime time = LocalTime.of(10, 1);

        TimeTable timeTable = TimeTable.builder().client(clientTable).date(date).time(time).build();

        when(clientService.getClientEntityById(userRequestId)).thenReturn(clientRequest);
        when(repository.findById(orderId)).thenReturn(Optional.of(timeTable));

        assertThrows(ClientMismatchException.class, () -> service.cancelReserve(cancelRequest));

        verify(clientService, times(1)).getClientEntityById(userRequestId);
        verify(repository, times(1)).findById(orderId);
    }

    //6 ----------------------------------------
    @Test
    void searchVisitsByNameAndDateTest() {
        LocalDate date = LocalDate.of(2025, 4, 21);

        String name = "Ivan";

        PoolVisitSearchRequest request = new PoolVisitSearchRequest(name, date);

        Client client = createClient();

        UUID orderId1 = UUID.randomUUID();
        UUID orderId2 = UUID.randomUUID();
        LocalTime time1 = LocalTime.of(10, 0);
        LocalTime time2 = LocalTime.of(11, 0);

        List<TimeTable> timeTables = List.of(
                TimeTable
                        .builder()
                        .orderId(orderId1).client(client).date(date).time(time1).build(),
                TimeTable
                        .builder()
                        .orderId(orderId2).client(client).date(date).time(time2).build()
        );

        List<VisitDto> visitDtos = List.of(
                new VisitDto(orderId1, time1),
                new VisitDto(orderId2, time2)
        );

        when(clientService.findByName(name)).thenReturn(client);
        when(repository.findByClientIdAndDateOrderByTime(1L, date)).thenReturn(timeTables);

        List<VisitDto> result = service.searchVisitsByNameAndDate(request);

        assertNotNull(result);

        assertEquals(result.get(0), visitDtos.get(0));
        assertEquals(result.get(1), visitDtos.get(1));

        verify(clientService, times(1)).findByName(name);
        verify(repository, times(1)).findByClientIdAndDateOrderByTime(1L, date);
    }

    @Test
    void searchVisitsByNameAndDateThrowClientNotFoundExceptionTest() {
        LocalDate date = LocalDate.of(2025, 4, 21);

        String name = "Ivan";

        PoolVisitSearchRequest request = new PoolVisitSearchRequest(name, date);

        when(clientService.findByName(request.getName())).thenThrow(ClientNotFoundException.class);

        assertThrows(ClientNotFoundException.class, () -> service.searchVisitsByNameAndDate(request));

        verify(clientService, times(1)).findByName(name);
        verify(repository, never()).findByClientIdAndDateOrderByTime(any(), any());
    }


}
