package ru.zuzex.practice.profilems.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zuzex.practice.profilems.dto.kafka.ProfileMessage;
import ru.zuzex.practice.profilems.dto.request.SearchParameters;
import ru.zuzex.practice.profilems.exception.exception.ProfileNotFoundException;
import ru.zuzex.practice.profilems.exception.exception.PageNotFound;
import ru.zuzex.practice.profilems.model.Profile;
import ru.zuzex.practice.profilems.repository.ProfileRepository;
import ru.zuzex.practice.profilems.security.JwtUser;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.BiFunction;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ProfileProducer profileProducer;
    private final ObjectMapper jacksonMapper;

    public Page<Profile> getAllProfiles(Integer page, Integer size) {
        var pageEntity = profileRepository.findAll(PageRequest.of(page - 1, size));

        if (pageEntity.getTotalPages() < page) throw new PageNotFound("Such page does not exist");

        return pageEntity;
    }

    public Page<Profile> search(SearchParameters searchParameters) {
        BiFunction<String, PageRequest, Page<Profile>> method = searchParameters.isIgnoreCase() ?
                profileRepository::searchAnywhereInNameOrSurnameIgnoreCase :
                profileRepository::searchAnywhereInNameOrSurname;

        var pageEntity = method.apply(searchParameters.getQuery(), searchParameters.getPageRequest());
        if (pageEntity.getTotalPages() < searchParameters.getPage()) throw new PageNotFound("Such page does not exist");

        return pageEntity;
    }

    public Profile getProfile(UUID profileId) {
        return profileRepository.findById(profileId).orElseThrow(ProfileNotFoundException::new);
    }

    @Transactional
    public Profile create(Profile profile) {
        var authenticatedUser = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var curUserId = authenticatedUser.getId();

        if(profileRepository.existsById(curUserId))
            throw new IllegalArgumentException("Profile for user : " + authenticatedUser.getUsername() + " already exists");

        profile.setProfileId(curUserId);
        var createdProfile = profileRepository.save(profile);

        sendKafkaMessage(curUserId, "CreateProfile");

        return createdProfile;
    }

    @Transactional
    public Profile update(UUID profileId, Profile profile) {
        if (!profileId.equals(profile.getProfileId()))
            throw new IllegalArgumentException("ID in path and body must match");

        var existedProfile = getProfile(profileId);

        existedProfile.setName(profile.getName());
        existedProfile.setSurname(profile.getSurname());
        existedProfile.setAge(profile.getAge());

        return profileRepository.save(existedProfile);
    }

    @Transactional
    public void delete(UUID profileId) {
        sendKafkaMessage(profileId, "DeleteProfile");
        profileRepository.deleteById(profileId);
    }

    @SneakyThrows
    private void sendKafkaMessage(UUID profileId, String messageType) {
        var profileMessage = ProfileMessage.builder()
                .id(UUID.randomUUID())
                .profileId(profileId)
                .eventType(messageType)
                .timestamp(LocalDateTime.now())
                .build();

        ObjectWriter ow = jacksonMapper.writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(profileMessage);

        profileProducer.sendMessage(json);
    }
}
