package ru.zuzex.practice.taskms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zuzex.practice.taskms.dto.kafka.ProfileMessage;
import ru.zuzex.practice.taskms.exception.exception.LocalProfileNotFoundException;
import ru.zuzex.practice.taskms.model.LocalProfile;
import ru.zuzex.practice.taskms.repository.LocalProfileRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProfileConsumer {
    private final ObjectMapper jacksonMapper;
    private final LocalProfileRepository localProfileRepository;

    @SneakyThrows
    @Transactional
    @KafkaListener(topics = "tasks-topic", groupId = "profile-group")
    public void listen(String message) {

        ProfileMessage profileMessage = jacksonMapper.readValue(message, ProfileMessage.class);
        System.out.println("Received Message: " + profileMessage);

        if (profileMessage.getEventType().equals("Deleteprofile")) {
            var localprofile = localProfileRepository.findById(profileMessage.getProfileId())
                    .orElseThrow(LocalProfileNotFoundException::new);

            localprofile.setIsActive(false);
            localprofile.setDeletedAt(LocalDateTime.now());
            localProfileRepository.save(localprofile);
//            taskRepository.deleteAllByprofileId(profileMessage.getprofileId());
        } else if (profileMessage.getEventType().equals("Createprofile")) {
            var newprofile = new LocalProfile();
            newprofile.setProfileId(profileMessage.getProfileId());
            newprofile.setIsActive(true);

            localProfileRepository.save(newprofile);
        } else System.out.println("Invalid event type: " + profileMessage.getEventType());
    }
}
