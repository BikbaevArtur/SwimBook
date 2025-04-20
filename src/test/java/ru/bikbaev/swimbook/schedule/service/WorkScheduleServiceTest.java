package ru.bikbaev.swimbook.schedule.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bikbaev.swimbook.schedule.dto.WorkTimeDto;
import ru.bikbaev.swimbook.schedule.mapper.WorkScheduleMapper;
import ru.bikbaev.swimbook.schedule.model.WorkSchedule;
import ru.bikbaev.swimbook.schedule.repository.JpaWorkSchedule;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkScheduleServiceTest {

    @Mock
    private JpaWorkSchedule repository;
    @Mock
    private WorkScheduleMapper mapper;
    @InjectMocks
    private WorkScheduleService service;

    @Test
    void getAllWorkScheduleTest(){
        List<WorkSchedule> schedules = List.of(
                WorkSchedule.builder()
                        .weekday(DayOfWeek.MONDAY)
                        .startTime(LocalTime.of(9, 0))
                        .endTime(LocalTime.of(18, 0))
                        .build(),
                WorkSchedule.builder()
                        .weekday(DayOfWeek.TUESDAY)
                        .startTime(LocalTime.of(10, 0))
                        .endTime(LocalTime.of(17, 0))
                        .build()
        );

        Map<DayOfWeek, WorkTimeDto> expected = Map.of(
                DayOfWeek.MONDAY, new WorkTimeDto(LocalTime.of(9, 0), LocalTime.of(18, 0)),
                DayOfWeek.TUESDAY, new WorkTimeDto(LocalTime.of(10, 0), LocalTime.of(17, 0))
        );

        when(repository.findAll()).thenReturn(schedules);
        when(mapper.convertWorkScheduleToMap(schedules)).thenReturn(expected);

        Map<DayOfWeek, WorkTimeDto> result = service.getAllWorkSchedule();

        assertNotNull(result);
        assertThat(result).isEqualTo(expected);
    }

}
