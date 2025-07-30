package ru.zuzex.practice.profilems.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.zuzex.practice.profilems.client.TaskFeignClient;
import ru.zuzex.practice.profilems.dto.ProfileDto;
import ru.zuzex.practice.profilems.dto.request.SearchParameters;
import ru.zuzex.practice.profilems.dto.response.PageResponse;
import ru.zuzex.practice.profilems.mapper.ProfileMapper;
import ru.zuzex.practice.profilems.model.Profile;
import ru.zuzex.practice.profilems.model.Task;
import ru.zuzex.practice.profilems.service.ProfileService;
import ru.zuzex.practice.profilems.validation.OnCreate;
import ru.zuzex.practice.profilems.validation.OnUpdate;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Tag(name = "Profile API", description = "Profile endpoints")
@RestController
@RequestMapping("api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    private final ProfileMapper profileMapper;
    private final TaskFeignClient taskFeignClient;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Receive all profiles",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved profiles"),
                    @ApiResponse(responseCode = "404", description = "Page not found")
            }
    )
    public ResponseEntity<PageResponse<ProfileDto>> getProfiles(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "5") Integer size
    ) {
        Page<Profile> pageEntity = profileService.getAllProfiles(page, size);
        var response = toPageResponse(pageEntity, profileMapper::toDto);

        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Search profiles by query(name or surname)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved profiles"),
                    @ApiResponse(responseCode = "404", description = "Profiles not found")
            }
    )
    public ResponseEntity<PageResponse<ProfileDto>> searchProfiles(
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

        Page<Profile> pageEntity = profileService.search(searchParams);
        var response = toPageResponse(pageEntity, profileMapper::toDto);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{profileId}")
    @PreAuthorize("@customSecurityExpression.hasPermission(#profileId)")
    @Operation(
            summary = "Receive profile and its Tasks by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved profile"),
                    @ApiResponse(responseCode = "404", description = "Profile not found")
            }
    )
    public ResponseEntity<ProfileDto> getProfileById(@PathVariable("profileId") UUID profileId) {
        var profile = profileService.getProfile(profileId);
        var profileDto = profileMapper.toDto(profile);

        List<Task> tasks;
        try {
            tasks = taskFeignClient.getTasks(profileId);
        } catch (Exception e) {
            tasks = List.of();
        }

        profileDto.setTasks(tasks);

        return ResponseEntity.ok().body(profileDto);
    }

    @PostMapping
    @Operation(
            summary = "Create new profile",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created profile"),
                    @ApiResponse(responseCode = "400", description = "ID should not be specified"),
                    @ApiResponse(responseCode = "400", description = "Validation failed")
            }
    )
    public ResponseEntity<ProfileDto> createProfile(@RequestBody @Validated(OnCreate.class) ProfileDto profileDto) {
        var profile = profileMapper.toEntity(profileDto);
        profile = profileService.create(profile);

        return ResponseEntity
                .created(URI.create("/api/v1/profile/" + profile.getProfileId()))
                .body(profileMapper.toDto(profile));
    }

    @PutMapping("/{profileId}")
    @PreAuthorize("@customSecurityExpression.isOwner(#profileId)")
    @Operation(
            summary = "Update profile",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated profile"),
                    @ApiResponse(responseCode = "400", description = "ID mismatch"),
                    @ApiResponse(responseCode = "400", description = "Validation failed")
            }
    )
    public ResponseEntity<ProfileDto> updateProfile(
            @PathVariable(name = "profileId") UUID profileId,
            @RequestBody @Validated(OnUpdate.class) ProfileDto profileDto) {
        var profile = profileMapper.toEntity(profileDto);
        profile = profileService.update(profileId, profile);

        return ResponseEntity.ok().body(profileMapper.toDto(profile));
    }


    @DeleteMapping("/{profileId}")
    @PreAuthorize("@customSecurityExpression.hasPermission(#profileId)")
    @Operation(
            summary = "Update profile",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted profile")
            }
    )
    public ResponseEntity<HttpStatus> deleteProfile(@PathVariable UUID profileId) {
        profileService.delete(profileId);
        return ResponseEntity.noContent().build();
    }

    private <T, R> PageResponse<R> toPageResponse(Page<T> pageEntity, Function<T, R> mapper) {
        List<R> profiles = pageEntity.getContent().stream().map(mapper).toList();

        return PageResponse.<R>builder()
                .content(profiles)
                .totalPages(pageEntity.getTotalPages())
                .totalElements(pageEntity.getTotalElements())
                .curPage(pageEntity.getNumber() + 1)
                .pageSize(pageEntity.getSize())
                .build();
    }
}
