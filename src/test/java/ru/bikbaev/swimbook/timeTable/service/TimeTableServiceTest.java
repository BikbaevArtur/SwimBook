package ru.bikbaev.swimbook.timeTable.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import ru.bikbaev.swimbook.schedule.service.ScheduleService;
import ru.bikbaev.swimbook.timeTable.repository.JpaTimeTable;

@ExtendWith(MockitoExtension.class)
public class TimeTableServiceTest {

    @Mock
    private JpaTimeTable repository;
    @Mock
    private ScheduleService scheduleService;
    @InjectMocks
    private TimeTableService service;

    @Value("${timetable.max-record-count}")
    private int MAX_COUNT_RECORD;

    @Test
    void getAllPoolTimetablesTest(){

    }

    @Test
    void getAvailableTimetablesForDateTest(){

    }
}
