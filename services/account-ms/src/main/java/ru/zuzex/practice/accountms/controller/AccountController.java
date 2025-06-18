package ru.zuzex.practice.accountms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.zuzex.practice.accountms.model.Account;
import ru.zuzex.practice.accountms.serviece.AccountService;

import java.util.List;

@RestController
@RequestMapping("api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<Account>> getAccounts() {
        return new ResponseEntity<>(accountService.getAllAccounts(), HttpStatus.OK);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccountById(@PathVariable("accountId") String accountId) {
        return new ResponseEntity<>(accountService.getAccount(accountId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Account> addAccount(@RequestBody Account account) {
        return new ResponseEntity<>(accountService.create(account), HttpStatus.CREATED);
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<Account> updateAccount(
            @PathVariable String accountId, @RequestBody Account account) {
        return new ResponseEntity<>(accountService.update(accountId, account), HttpStatus.OK);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<HttpStatus> deleteAccount(@PathVariable String accountId) {
        accountService.delete(accountId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
