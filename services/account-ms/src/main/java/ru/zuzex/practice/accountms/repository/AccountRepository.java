package ru.zuzex.practice.accountms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.zuzex.practice.accountms.model.Account;

import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    @Query("SELECT a FROM Account a WHERE a.name LIKE %:keyword% OR a.surname LIKE %:keyword%")
    Page<Account> searchAnywhereInNameOrSurname(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            SELECT a FROM Account a WHERE
            LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(a.surname) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<Account> searchAnywhereInNameOrSurnameIgnoreCase(@Param("keyword") String keyword, Pageable pageable);
}
