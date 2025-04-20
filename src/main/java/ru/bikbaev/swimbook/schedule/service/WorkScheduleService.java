package ru.bikbaev.swimbook.schedule.service;

import org.springframework.stereotype.Service;
import ru.bikbaev.swimbook.schedule.dto.WorkTimeDto;
import ru.bikbaev.swimbook.schedule.mapper.WorkScheduleMapper;
import ru.bikbaev.swimbook.schedule.repository.JpaWorkSchedule;

import java.time.DayOfWeek;
import java.util.Map;

@Service
public class WorkScheduleService {
    private final JpaWorkSchedule repository;
    private final WorkScheduleMapper mapper;

    public WorkScheduleService(JpaWorkSchedule repository, WorkScheduleMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Получает полное недельное рабочее расписание.
     * Метод запрашивает все записи рабочего расписания из репозитория
     * и преобразует их в Map, где ключом является день недели
     * а значением — график работы.
     *
     * @return Map<DayOfWeek, WorkTimeDto>, DayOfWeek - день недели, WorkTimeDto - график работы
     */

    public Map<DayOfWeek, WorkTimeDto> getAllWorkSchedule() {
        return mapper.convertWorkScheduleToMap(
                repository.findAll()
        );
    }

}
