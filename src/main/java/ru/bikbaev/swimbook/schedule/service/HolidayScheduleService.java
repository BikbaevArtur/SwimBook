package ru.bikbaev.swimbook.schedule.service;

import org.springframework.stereotype.Service;
import ru.bikbaev.swimbook.schedule.dto.WorkTimeDto;
import ru.bikbaev.swimbook.schedule.mapper.HolidayScheduleMapper;
import ru.bikbaev.swimbook.schedule.repository.JpaHolidaySchedule;

import java.time.LocalDate;
import java.util.Map;

@Service
public class HolidayScheduleService {
    private final HolidayScheduleMapper mapper;
    private final JpaHolidaySchedule jpa;

    public HolidayScheduleService(HolidayScheduleMapper mapper, JpaHolidaySchedule jpa) {
        this.mapper = mapper;
        this.jpa = jpa;
    }

    /**
     * Возвращает карту актуальных праздничных дат с соответствующим рабочим временем.
     * Метод запрашивает у репозитория все праздничные расписания, начиная с текущей даты,
     * и преобразует их в Map, где ключом является дата праздника,
     * а значением - рабочий график
     *
     * @return  Map<LocalDate, WorkTimeDto>, LocalDate - праздничный день, WorkTimeDto - график работы
     */
    public Map<LocalDate, WorkTimeDto> getValidHolidays() {
        return mapper
                .convertHolidayScheduleListToMap(
                        jpa.findAllFromDate(LocalDate.now())
                );
    }

}
