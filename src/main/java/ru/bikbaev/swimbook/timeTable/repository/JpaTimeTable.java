package ru.bikbaev.swimbook.timeTable.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bikbaev.swimbook.client.model.Client;
import ru.bikbaev.swimbook.timeTable.model.TimeTable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface JpaTimeTable extends JpaRepository<TimeTable, UUID> {
    List<TimeTable> findByDateOrderByTime(LocalDate date);
    List<TimeTable> findByClientIdAndDateOrderByTime(Long clientId,LocalDate date);
}
