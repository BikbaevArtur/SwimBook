package ru.bikbaev.swimbook.schedule.mapper;

import org.springframework.stereotype.Component;
import ru.bikbaev.swimbook.schedule.dto.WorkTimeDto;
import ru.bikbaev.swimbook.schedule.model.HolidaySchedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class HolidayScheduleMapper {


    public Map<LocalDate, WorkTimeDto> convertHolidayScheduleListToMap(List<HolidaySchedule> schedules) {
        return schedules.stream()
                .collect(Collectors.toMap(
                        HolidaySchedule::getDate,
                        e -> new WorkTimeDto(e.getStartTime(), e.getEndTime())
                ));
    }
}
