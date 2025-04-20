package ru.bikbaev.swimbook.schedule.mapper;

import org.junit.jupiter.api.Test;
import ru.bikbaev.swimbook.schedule.dto.WorkTimeDto;
import ru.bikbaev.swimbook.schedule.model.WorkSchedule;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class WorkScheduleMapperTest {

    private final WorkScheduleMapper mapper = new WorkScheduleMapper();

    @Test
    void convertWorkScheduleToMapTest() {
        List<WorkSchedule> workSchedules = List.of(
                WorkSchedule.builder()
                        .weekday(DayOfWeek.FRIDAY)
                        .startTime(LocalTime.of(9, 0))
                        .endTime(LocalTime.of(17, 0))
                        .build(),
                WorkSchedule.builder()
                        .weekday(DayOfWeek.SATURDAY)
                        .startTime(LocalTime.of(10, 0))
                        .endTime(LocalTime.of(16, 0))
                        .build(),
                WorkSchedule.builder()
                        .weekday(DayOfWeek.SUNDAY)
                        .startTime(LocalTime.of(11, 0))
                        .endTime(LocalTime.of(15, 0))
                        .build()
        );

        Map<DayOfWeek, WorkTimeDto> result = mapper.convertWorkScheduleToMap(workSchedules);

        assertThat(result).hasSize(3);
        assertThat(result.get(DayOfWeek.FRIDAY))
                .isEqualTo(new WorkTimeDto(LocalTime.of(9, 0), LocalTime.of(17, 0)));
        assertThat(result.get(DayOfWeek.SATURDAY))
                .isEqualTo(new WorkTimeDto(LocalTime.of(10, 0), LocalTime.of(16, 0)));
        assertThat(result.get(DayOfWeek.SUNDAY))
                .isEqualTo(new WorkTimeDto(LocalTime.of(11, 0), LocalTime.of(15, 0)));
    }
}
