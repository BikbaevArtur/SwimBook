package ru.bikbaev.swimbook.schedule.mapper;

import org.springframework.stereotype.Component;
import ru.bikbaev.swimbook.schedule.dto.WorkTimeDto;
import ru.bikbaev.swimbook.schedule.model.WorkSchedule;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class WorkScheduleMapper {

    public Map<DayOfWeek, WorkTimeDto> convertWorkScheduleToMap(List<WorkSchedule> workScheduleList) {
        return workScheduleList
                .stream()
                .collect(Collectors.toMap(
                        WorkSchedule::getWeekday,
                        e -> new WorkTimeDto(e.getStartTime(), e.getEndTime())
                ));
    }
}
