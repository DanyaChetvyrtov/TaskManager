package ru.zuzex.practice.accountms.mapper;

import org.mapstruct.Mapper;
import ru.zuzex.practice.accountms.dto.AccountDto;
import ru.zuzex.practice.accountms.model.Account;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountDto toDto(Account account);

    Account toEntity(AccountDto accountDto);
}
