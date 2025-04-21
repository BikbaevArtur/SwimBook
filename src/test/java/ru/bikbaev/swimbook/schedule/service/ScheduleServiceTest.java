package ru.bikbaev.swimbook.schedule.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bikbaev.swimbook.schedule.dto.WorkTimeDto;
import ru.bikbaev.swimbook.timeTable.dto.TimeRecord;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceTest {
    @Mock
    private WorkScheduleService workScheduleService;
    @Mock
    private HolidayScheduleService holidayScheduleService;
    @InjectMocks
    @Spy
    private ScheduleService service;

    @Test
    void getScheduleForNext60DaysTest(){

        WorkTimeDto workTime = new WorkTimeDto(LocalTime.of(9, 0), LocalTime.of(18, 0));

        Map<DayOfWeek, WorkTimeDto> workSchedule = new HashMap<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            workSchedule.put(day, workTime);
        }

        LocalDate now = LocalDate.now();
        LocalDate date1 = now.plusDays(10);
        LocalDate date2 = now.plusDays(20);

        Map<LocalDate, WorkTimeDto> holidaySchedule = Map.of(
                date1, new WorkTimeDto(LocalTime.of(10, 0), LocalTime.of(16, 0)),
                date2, new WorkTimeDto(LocalTime.of(9, 0), LocalTime.of(15, 0))
        );

        when(workScheduleService.getAllWorkSchedule()).thenReturn(workSchedule);
        when(holidayScheduleService.getValidHolidays()).thenReturn(holidaySchedule);

        Map<LocalDate,WorkTimeDto> result = service.getScheduleForNext60Days();

        assertNotNull(result);
        assertEquals(60,result.size());

        for(Map.Entry<LocalDate,WorkTimeDto> holiday: holidaySchedule.entrySet()){
            assertEquals(holiday.getValue(),result.get(holiday.getKey()));
        }

        LocalDate someNormalDay = now.plusDays(2);
        if (!holidaySchedule.containsKey(someNormalDay)) {
            assertEquals(workTime, result.get(someNormalDay));
        }

        verify(workScheduleService, times(1)).getAllWorkSchedule();
        verify(holidayScheduleService, times(1)).getValidHolidays();

    }

    @Test
    void createTimeRecordsForScheduleTest(){

        LocalDate testDate = LocalDate.of(2025, 4, 21);

        WorkTimeDto workTimeDto = new WorkTimeDto(LocalTime.of(9, 0), LocalTime.of(12, 0));

        Map<LocalDate, WorkTimeDto> scheduleMap = new HashMap<>();
        scheduleMap.put(testDate, workTimeDto);

        doReturn(scheduleMap).when(service).getScheduleForNext60Days();

        List<TimeRecord> timeRecords = service.createTimeRecordsForSchedule(testDate);

        assertNotNull(timeRecords);
        assertEquals(3, timeRecords.size());

        assertEquals(LocalTime.of(9, 0), timeRecords.get(0).getTime());
        assertEquals(LocalTime.of(10, 0), timeRecords.get(1).getTime());
        assertEquals(LocalTime.of(11, 0), timeRecords.get(2).getTime());

        assertEquals(0, timeRecords.get(0).getCount());
        assertEquals(0, timeRecords.get(1).getCount());
        assertEquals(0, timeRecords.get(2).getCount());

        verify(service, times(1)).getScheduleForNext60Days();
    }

    @Test
    void validatePoolWorkingHoursTest(){
        LocalDate date = LocalDate.of(2025,4,21);
        LocalTime startTime = LocalTime.of(9,0);
        LocalTime endTime = LocalTime.of(18,0);

        Map<LocalDate,WorkTimeDto> schedule = Map.of(date,new WorkTimeDto(startTime,endTime));

        when(service.getScheduleForNext60Days()).thenReturn(schedule);

        LocalTime testTime0 = LocalTime.of(8,0);
        LocalTime testTime1 = LocalTime.of(9,0);
        LocalTime testTime2 = LocalTime.of(10,0);
        LocalTime testTime3 = LocalTime.of(18,0);

        boolean result0 = service.validatePoolWorkingHours(date,testTime0);
        boolean result1 = service.validatePoolWorkingHours(date,testTime1);
        boolean result2 = service.validatePoolWorkingHours(date,testTime2);
        boolean result3 = service.validatePoolWorkingHours(date,testTime3);

        assertFalse(result0);
        assertTrue(result1);
        assertTrue(result2);
        assertFalse(result3);

    }

    @Test
    void validatePoolWorkingIntervalTest(){
        LocalDate date = LocalDate.of(2025,4,21);
        LocalTime startTime = LocalTime.of(9,0);
        LocalTime endTime = LocalTime.of(18,0);

        Map<LocalDate,WorkTimeDto> schedule = Map.of(date,new WorkTimeDto(startTime,endTime));

        when(service.getScheduleForNext60Days()).thenReturn(schedule);

        LocalTime startTestTime0 = LocalTime.of(8,0);
        LocalTime endTestTime0 = LocalTime.of(12,0);

        LocalTime startTestTime1 = LocalTime.of(9,0);
        LocalTime endTestTime1 = LocalTime.of(12,0);

        LocalTime startTestTime2 = LocalTime.of(15,0);
        LocalTime endTestTime2 = LocalTime.of(17,0);

        LocalTime startTestTime3 = LocalTime.of(15,0);
        LocalTime endTestTime3 = LocalTime.of(18,0);

        boolean result0= service.validatePoolWorkingInterval(date,startTestTime0,endTestTime0);
        boolean result1 = service.validatePoolWorkingInterval(date,startTestTime1,endTestTime1);
        boolean result2 = service.validatePoolWorkingInterval(date,startTestTime2,endTestTime2);
        boolean result3 = service.validatePoolWorkingInterval(date,startTestTime3,endTestTime3);

        assertFalse(result0);
        assertTrue(result1);
        assertTrue(result2);
        assertFalse(result3);
    }
}
