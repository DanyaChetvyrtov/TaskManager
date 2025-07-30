package ru.zuzex.practice.profilems.mapper;

import org.mapstruct.Mapper;
import ru.zuzex.practice.profilems.dto.ProfileDto;
import ru.zuzex.practice.profilems.model.Profile;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileDto toDto(Profile profile);

    Profile toEntity(ProfileDto profileDto);
}
