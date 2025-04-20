package ru.bikbaev.swimbook.schedule.mapper;

import org.junit.jupiter.api.Test;
import ru.bikbaev.swimbook.schedule.dto.WorkTimeDto;
import ru.bikbaev.swimbook.schedule.model.HolidaySchedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class HolidayScheduleMapperTest {

    private final HolidayScheduleMapper mapper = new HolidayScheduleMapper();

    @Test
    void convertHolidayScheduleListToMapTest() {
        List<HolidaySchedule> schedules = List.of(
                HolidaySchedule.builder()
                        .date(LocalDate.of(2025, 5, 1))
                        .startTime(LocalTime.of(10, 0))
                        .endTime(LocalTime.of(16, 0))
                        .build(),
                HolidaySchedule.builder()
                        .date(LocalDate.of(2025, 5, 9))
                        .startTime(LocalTime.of(11, 0))
                        .endTime(LocalTime.of(17, 0))
                        .build()
        );

        Map<LocalDate, WorkTimeDto> result = mapper.convertHolidayScheduleListToMap(schedules);

        assertThat(result).hasSize(2);
        assertThat(result.get(LocalDate.of(2025, 5, 1)))
                .isEqualTo(new WorkTimeDto(LocalTime.of(10, 0), LocalTime.of(16, 0)));
        assertThat(result.get(LocalDate.of(2025, 5, 9)))
                .isEqualTo(new WorkTimeDto(LocalTime.of(11, 0), LocalTime.of(17, 0)));
    }

}
