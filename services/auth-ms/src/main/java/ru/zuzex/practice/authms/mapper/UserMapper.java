package ru.zuzex.practice.authms.mapper;

import org.mapstruct.Mapper;
import ru.zuzex.practice.authms.dto.UserDto;
import ru.zuzex.practice.authms.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto userWriteDto);

}