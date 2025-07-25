package ru.zuzex.practice.accountms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zuzex.practice.accountms.dto.kafka.AccountMessage;
import ru.zuzex.practice.accountms.dto.request.SearchParameters;
import ru.zuzex.practice.accountms.exception.exception.AccountNotFoundException;
import ru.zuzex.practice.accountms.exception.exception.PageNotFound;
import ru.zuzex.practice.accountms.model.Account;
import ru.zuzex.practice.accountms.repository.AccountRepository;
import ru.zuzex.practice.accountms.security.JwtUser;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.BiFunction;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountProducer accountProducer;
    private final ObjectMapper jacksonMapper;

    public Page<Account> getAllAccounts(Integer page, Integer size) {
        var pageEntity = accountRepository.findAll(PageRequest.of(page - 1, size));

        if (pageEntity.getTotalPages() < page) throw new PageNotFound("Such page does not exist");

        return pageEntity;
    }

    public Page<Account> search(SearchParameters searchParameters) {
        BiFunction<String, PageRequest, Page<Account>> method = searchParameters.isIgnoreCase() ?
                accountRepository::searchAnywhereInNameOrSurnameIgnoreCase :
                accountRepository::searchAnywhereInNameOrSurname;

        var pageEntity = method.apply(searchParameters.getQuery(), searchParameters.getPageRequest());
        if (pageEntity.getTotalPages() < searchParameters.getPage()) throw new PageNotFound("Such page does not exist");

        return pageEntity;
    }

    public Account getAccount(UUID accountId) {
        return accountRepository.findById(accountId).orElseThrow(AccountNotFoundException::new);
    }

    @Transactional
    public Account create(Account account) {
        var authenticatedUser = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var curUserId = authenticatedUser.getId();

        if(accountRepository.existsById(curUserId))
            throw new IllegalArgumentException("Account for user : " + authenticatedUser.getUsername() + " already exists");

        account.setAccountId(curUserId);
        var createdAccount = accountRepository.save(account);

        sendKafkaMessage(curUserId, "CreateAccount");

        return createdAccount;
    }

    @Transactional
    public Account update(UUID accountId, Account account) {
        if (!accountId.equals(account.getAccountId()))
            throw new IllegalArgumentException("ID in path and body must match");

        var existedAccount = getAccount(accountId);

        existedAccount.setName(account.getName());
        existedAccount.setSurname(account.getSurname());
        existedAccount.setAge(account.getAge());

        return accountRepository.save(existedAccount);
    }

    @Transactional
    public void delete(UUID accountId) {
        sendKafkaMessage(accountId, "DeleteAccount");
        accountRepository.deleteById(accountId);
    }

    @SneakyThrows
    private void sendKafkaMessage(UUID accountId, String messageType) {
        var accountMessage = AccountMessage.builder()
                .id(UUID.randomUUID())
                .accountId(accountId)
                .eventType(messageType)
                .timestamp(LocalDateTime.now())
                .build();

        ObjectWriter ow = jacksonMapper.writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(accountMessage);

        accountProducer.sendMessage(json);
    }
}
