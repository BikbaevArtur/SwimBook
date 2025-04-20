package ru.bikbaev.swimbook.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bikbaev.swimbook.schedule.model.WorkSchedule;

import java.time.DayOfWeek;

public interface JpaWorkSchedule extends JpaRepository<WorkSchedule, DayOfWeek> {
}
