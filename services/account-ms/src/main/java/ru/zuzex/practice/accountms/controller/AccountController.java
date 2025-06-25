package ru.zuzex.practice.accountms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.zuzex.practice.accountms.client.TaskFeignClient;
import ru.zuzex.practice.accountms.dto.AccountDto;
import ru.zuzex.practice.accountms.dto.request.SearchParameters;
import ru.zuzex.practice.accountms.dto.response.PageResponse;
import ru.zuzex.practice.accountms.mapper.AccountMapper;
import ru.zuzex.practice.accountms.model.Account;
import ru.zuzex.practice.accountms.model.Task;
import ru.zuzex.practice.accountms.service.AccountService;
import ru.zuzex.practice.accountms.validation.OnCreate;
import ru.zuzex.practice.accountms.validation.OnUpdate;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Tag(name = "Account API", description = "Account endpoints")
@RestController
@RequestMapping("api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final AccountMapper accountMapper;
    private final TaskFeignClient taskFeignClient;

    @GetMapping
    @Operation(
            summary = "Receive all accounts",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved accounts"),
                    @ApiResponse(responseCode = "404", description = "Page not found")
            }
    )
    public ResponseEntity<PageResponse<AccountDto>> getAccounts(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "5") Integer size
    ) {
        Page<Account> pageEntity = accountService.getAllAccounts(page, size);
        var response = toPageResponse(pageEntity, accountMapper::toDto);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search accounts by query(name or surname)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved accounts"),
                    @ApiResponse(responseCode = "404", description = "Accounts not found")
            }
    )
    public ResponseEntity<PageResponse<AccountDto>> searchAccounts(
            @RequestParam("query") String query,
            @RequestParam(value = "ignoreCase", required = false, defaultValue = "false") boolean ignoreCase,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "5") Integer size
    ) {
        var searchParams = SearchParameters.builder()
                .query(query)
                .ignoreCase(ignoreCase)
                .page(page)
                .size(size)
                .build();

        Page<Account> pageEntity = accountService.search(searchParams);
        var response = toPageResponse(pageEntity, accountMapper::toDto);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{accountId}")
    @Operation(
            summary = "Receive account and its Tasks by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved account"),
                    @ApiResponse(responseCode = "404", description = "Account not found")
            }
    )
    public ResponseEntity<AccountDto> getAccountById(@PathVariable("accountId") UUID accountId) {
        var account = accountService.getAccount(accountId);
        var accountDto = accountMapper.toDto(account);

        List<Task> tasks;
        try {
            tasks = taskFeignClient.getTasks(accountId);
        } catch (Exception e) {
            tasks = List.of();
        }

        accountDto.setTasks(tasks);

        return ResponseEntity.ok().body(accountDto);
    }

    @PostMapping
    @Operation(
            summary = "Create new account",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created account"),
                    @ApiResponse(responseCode = "400", description = "ID should not be specified"),
                    @ApiResponse(responseCode = "400", description = "Validation failed")
            }
    )
    public ResponseEntity<AccountDto> createAccount(@RequestBody @Validated(OnCreate.class) AccountDto accountDto) {
        var account = accountMapper.toEntity(accountDto);
        account = accountService.create(account);

        return ResponseEntity
                .created(URI.create("/api/v1/account/" + account.getAccountId()))
                .body(accountMapper.toDto(account));
    }

    @PutMapping("/{accountId}")
    @Operation(
            summary = "Update account",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated account"),
                    @ApiResponse(responseCode = "400", description = "ID mismatch"),
                    @ApiResponse(responseCode = "400", description = "Validation failed")
            }
    )
    public ResponseEntity<AccountDto> updateAccount(
            @PathVariable(name = "accountId") UUID accountId,
            @RequestBody @Validated(OnUpdate.class) AccountDto accountDto) {
        var account = accountMapper.toEntity(accountDto);
        account = accountService.update(accountId, account);

        return ResponseEntity.ok().body(accountMapper.toDto(account));
    }

    @DeleteMapping("/{accountId}")
    @Operation(
            summary = "Update account",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted account")
            }
    )
    public ResponseEntity<HttpStatus> deleteAccount(@PathVariable UUID accountId) {
        accountService.delete(accountId);
        return ResponseEntity.noContent().build();
    }

    private <T, R> PageResponse<R> toPageResponse(Page<T> pageEntity, Function<T, R> mapper) {
        List<R> accounts = pageEntity.getContent().stream().map(mapper).toList();

        return PageResponse.<R>builder()
                .content(accounts)
                .totalPages(pageEntity.getTotalPages())
                .totalElements(pageEntity.getTotalElements())
                .curPage(pageEntity.getNumber() + 1)
                .pageSize(pageEntity.getSize())
                .build();
    }
}
