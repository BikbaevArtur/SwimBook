package ru.bikbaev.swimbook.client.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bikbaev.swimbook.client.model.Client;

import java.util.Optional;

@Repository
public interface JpaClient extends JpaRepository<Client,Long> {
    boolean existsByEmailOrPhone(String email,String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    Optional<Client> findByName(String name);
}
