package ru.zuzex.practice.taskms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.zuzex.practice.taskms.model.LocalAccount;

import java.util.UUID;

@Repository
public interface LocalAccountRepository extends JpaRepository<LocalAccount, UUID> {
}
