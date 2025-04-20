package ru.bikbaev.swimbook.schedule.service;

import org.springframework.stereotype.Service;
import ru.bikbaev.swimbook.schedule.dto.WorkTimeDto;
import ru.bikbaev.swimbook.timeTable.dto.TimeRecord;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class ScheduleService {
    private final WorkScheduleService workScheduleService;
    private final HolidayScheduleService holidayScheduleService;

    public ScheduleService(WorkScheduleService workScheduleService, HolidayScheduleService holidayScheduleService) {
        this.workScheduleService = workScheduleService;
        this.holidayScheduleService = holidayScheduleService;
    }

    /**
     * Возвращает расписание работы на ближайшие 60 дней.
     * Если на дату есть праздничное расписание — оно используется.
     * Иначе используется обычное расписание по дню недели.
     *
     * @return Map<LocalDate, WorkTimeDto>, LocalDate - дата, WorkTimeDto - график работы
     */
    public Map<LocalDate, WorkTimeDto> getScheduleForNext60Days() {
        Map<LocalDate, WorkTimeDto> schedule = new HashMap<>();

        Map<DayOfWeek, WorkTimeDto> workScheduleDtos = workScheduleService.getAllWorkSchedule();
        Map<LocalDate, WorkTimeDto> holidayScheduleDtos = holidayScheduleService.getValidHolidays();

        LocalDate date = LocalDate.now();
        for (int i = 0; i < 60; i++) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (!holidayScheduleDtos.containsKey(date)) {
                schedule.put(date, workScheduleDtos.get(dayOfWeek));
            } else {
                schedule.put(date, holidayScheduleDtos.get(date));
            }
            date = date.plusDays(1);
        }
        return schedule;
    }



    /**
     * Возвращает список часовых интервалов для указанной даты на основе расписания.
     * в виде LocalTime - время и count - кол-во записей
     * Если на дату нет расписания — возвращается пустой список.
     *
     * @param localDate дата, для которой формируются интервалы
     * @return список временных интервалов
     */
    public List<TimeRecord> createTimeRecordsForSchedule(LocalDate localDate){
        Map<LocalDate,WorkTimeDto> scheduleWork = getScheduleForNext60Days();

        WorkTimeDto scheduleDay = scheduleWork.get(localDate);

        if(scheduleDay == null){
            return Collections.emptyList();
        }

        List<TimeRecord> recordList =  new ArrayList<>();

        LocalTime startTime = scheduleDay.getStartTime();
        LocalTime endTime = scheduleDay.getEndTime();

        while(!startTime.isAfter(endTime)){
            recordList.add(new TimeRecord(startTime,0));
           startTime = startTime.plusHours(1);
        }

        return recordList;
    }

}
