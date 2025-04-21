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
     * TODO:сделать кэширование
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
    public List<TimeRecord> createTimeRecordsForSchedule(LocalDate localDate) {
        Map<LocalDate, WorkTimeDto> scheduleWork = getScheduleForNext60Days();

        WorkTimeDto scheduleDay = scheduleWork.get(localDate);

        if (scheduleDay == null) {
            return Collections.emptyList();
        }

        List<TimeRecord> recordList = new ArrayList<>();

        LocalTime startTime = scheduleDay.getStartTime();
        LocalTime endTime = scheduleDay.getEndTime();

        while (startTime.isBefore(endTime)) {
            recordList.add(new TimeRecord(startTime, 0));
            startTime = startTime.plusHours(1);
        }

        return recordList;
    }


    /**
     * Проверка работы бассейна в определенную дату и время
     *
     * @param date дата работы
     * @param time время работы
     * @return возвращает булевое значение
     */
    public boolean validatePoolWorkingHours(LocalDate date, LocalTime time) {
        Map<LocalDate, WorkTimeDto> schedule = getScheduleForNext60Days();
        if (!schedule.containsKey(date)) {
            return false;
        }

        WorkTimeDto scheduleDay = schedule.get(date);

        LocalTime poolOpenTime = scheduleDay.getStartTime();
        LocalTime poolCloseTime = scheduleDay.getEndTime();

        return !time.isBefore(poolOpenTime) && time.isBefore(poolCloseTime);
    }


    /**
     * Проверяет, открыт ли бассейн в течение всего интервала
     *
     * @param date      дата, к которой относится интервал
     * @param startTime начало интервала
     * @param endTime   конец интервала
     * @return true — если интервал полностью попадает в рабочее время / false если нет
     */
    public boolean validatePoolWorkingInterval(LocalDate date, LocalTime startTime, LocalTime endTime) {
        Map<LocalDate, WorkTimeDto> schedule = getScheduleForNext60Days();
        WorkTimeDto scheduleDay = schedule.get(date);

        if (scheduleDay == null) {
            return false;
        }

        LocalTime poolOpenTime = scheduleDay.getStartTime();
        LocalTime poolCloseTime = scheduleDay.getEndTime();

        return !startTime.isBefore(poolOpenTime) && endTime.isBefore(poolCloseTime);
    }

}
