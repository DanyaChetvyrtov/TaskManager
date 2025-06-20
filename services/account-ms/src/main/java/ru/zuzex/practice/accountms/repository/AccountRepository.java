package ru.zuzex.practice.accountms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.zuzex.practice.accountms.model.Account;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    @Query("SELECT a FROM Account a WHERE a.name LIKE %:keyword% OR a.surname LIKE %:keyword%")
    List<Account> searchAnywhereInNameOrSurname(@Param("keyword") String keyword);
}
