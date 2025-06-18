package ru.zuzex.practice.accountms.serviece;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zuzex.practice.accountms.model.Account;
import ru.zuzex.practice.accountms.repository.AccountRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {
    private final AccountRepository accountRepository;

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccount(String accountId) {
        return accountRepository.findById(UUID.fromString(accountId))
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountId));
    }

    @Transactional
    public Account create(Account account) {
        return accountRepository.save(account);
    }

    @Transactional
    public Account update(String accountId, Account account) {
        var existerAccount = getAccount(accountId);

        existerAccount.setName(account.getName());
        existerAccount.setSurname(account.getSurname());
        existerAccount.setAge(account.getAge());

        return accountRepository.save(existerAccount);
    }

    @Transactional
    public void delete(String accountId) {
        accountRepository.deleteById(UUID.fromString(accountId));
    }
}
