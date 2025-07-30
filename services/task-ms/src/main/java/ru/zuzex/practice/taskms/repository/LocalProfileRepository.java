package ru.zuzex.practice.taskms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.zuzex.practice.taskms.model.LocalProfile;

import java.util.UUID;

@Repository
public interface LocalProfileRepository extends JpaRepository<LocalProfile, UUID> {
}
