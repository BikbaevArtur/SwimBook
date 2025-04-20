package ru.bikbaev.swimbook.schedule.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bikbaev.swimbook.schedule.dto.WorkTimeDto;
import ru.bikbaev.swimbook.schedule.mapper.HolidayScheduleMapper;
import ru.bikbaev.swimbook.schedule.model.HolidaySchedule;
import ru.bikbaev.swimbook.schedule.repository.JpaHolidaySchedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HolidayScheduleServiceTest {

    @Mock
    private HolidayScheduleMapper mapper;
    @Mock
    private JpaHolidaySchedule repository;
    @InjectMocks
    private HolidayScheduleService service;

    @Test
    void getValidHolidaysTest() {
        LocalDate now = LocalDate.now();
        LocalDate date1 = now.plusDays(10);
        LocalDate date2 = now.plusDays(20);

        List<HolidaySchedule> holidays = List.of(
                HolidaySchedule.builder()
                        .date(date1)
                        .startTime(LocalTime.of(10, 0))
                        .endTime(LocalTime.of(16, 0))
                        .build(),
                HolidaySchedule.builder()
                        .date(date2)
                        .startTime(LocalTime.of(9, 0))
                        .endTime(LocalTime.of(15, 0))
                        .build());

        Map<LocalDate, WorkTimeDto> expectedMap = Map.of(
                date1, new WorkTimeDto(LocalTime.of(10, 0), LocalTime.of(16, 0)),
                date2, new WorkTimeDto(LocalTime.of(9, 0), LocalTime.of(15, 0))
        );

        when(repository.findAllFromDate(now)).thenReturn(holidays);
        when(mapper.convertHolidayScheduleListToMap(holidays)).thenReturn(expectedMap);

        Map<LocalDate, WorkTimeDto> result = service.getValidHolidays();

        assertNotNull(result);
        assertEquals(expectedMap, result);

        verify(repository, times(1)).findAllFromDate(now);
        verify(mapper, times(1)).convertHolidayScheduleListToMap(holidays);
    }

}
