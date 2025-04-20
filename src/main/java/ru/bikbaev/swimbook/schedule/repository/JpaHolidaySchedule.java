package ru.bikbaev.swimbook.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.bikbaev.swimbook.schedule.model.HolidaySchedule;

import java.time.LocalDate;
import java.util.List;

public interface JpaHolidaySchedule extends JpaRepository<HolidaySchedule, LocalDate> {
    @Query("SELECT holiday FROM HolidaySchedule holiday WHERE holiday.date >= :date")
    List<HolidaySchedule> findAllFromDate(@Param("date") LocalDate date);
}
