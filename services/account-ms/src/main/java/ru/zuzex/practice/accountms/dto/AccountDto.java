package ru.zuzex.practice.accountms.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.zuzex.practice.accountms.validation.OnCreate;
import ru.zuzex.practice.accountms.validation.OnUpdate;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
    @NotNull(message = "Id can't be null", groups = OnUpdate.class)
    private UUID accountId;

    @NotNull(message = "Name must be not null", groups = {OnCreate.class, OnUpdate.class})
    @Length(min = 2, max = 100, message = "Name should be in range between 2 and 100 chars", groups = {OnCreate.class, OnUpdate.class})
    private String name;

    @NotNull(message = "Surname must be not null", groups = {OnCreate.class, OnUpdate.class})
    @Length(min = 2, max = 100, message = "Surname should be in range between 2 and 100 chars", groups = {OnCreate.class, OnUpdate.class})
    private String surname;

    @NotNull(message = "age must be not null", groups = {OnCreate.class, OnUpdate.class})
    @Min(value = 0, message = "Age can't be negative", groups = {OnCreate.class, OnUpdate.class})
    @Max(value = 100, message = "Age should be less or equal 100", groups = {OnCreate.class, OnUpdate.class})
    private Integer age;
}
