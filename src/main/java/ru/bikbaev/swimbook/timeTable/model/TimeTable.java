package ru.bikbaev.swimbook.timeTable.model;


import jakarta.persistence.*;
import lombok.*;
import ru.bikbaev.swimbook.client.model.Client;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "time_table")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Builder
public class TimeTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id")
    @EqualsAndHashCode.Include
    private UUID orderId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    /**
     * TODO n+1
     */
    private Client client;
    @Column(name = "date", nullable = false)
    private LocalDate date;
    @Column(name = "time", nullable = false)
    private LocalTime time;
}
