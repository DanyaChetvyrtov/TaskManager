package ru.zuzex.practice.taskms.security.expressions;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.zuzex.practice.taskms.security.JwtUser;
import ru.zuzex.practice.taskms.security.Role;
import ru.zuzex.practice.taskms.service.TaskService;

import java.util.Set;
import java.util.UUID;

@Component("customSecurityExpression")
@RequiredArgsConstructor
public class CustomSecurityExpressions {
    private final TaskService taskService;

    public boolean hasPermissionTask(UUID taskId) {
        var user = getAuthenticatedUser();

        return hasAnyRole(user.getRoles(), Role.ROLE_ADMIN) || isOwner(taskId, user);
    }

    public boolean hasPermissionTaskS_(UUID accountId) {
        var user = getAuthenticatedUser();

        return hasAnyRole(user.getRoles(), Role.ROLE_ADMIN) || accountId.equals(user.getId());
    }

    private boolean isOwner(UUID taskId, JwtUser user) {
        var requestedTask = taskService.getTask(taskId);
        var ownerId = requestedTask.getAccountId();

        return ownerId.equals(user.getId());
    }

    public boolean isOwner(UUID taskId) {
        var authenticatedUser = getAuthenticatedUser();
        var userId = authenticatedUser.getId();
        var requestedTask = taskService.getTask(taskId);

        return userId.equals(requestedTask.getAccountId());
    }

    private JwtUser getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (JwtUser) authentication.getPrincipal();
    }

    private boolean hasAnyRole(Set<Role> curUserRoles, Role... roles) {
        for (Role role : roles)
            if (curUserRoles.contains(role)) return true;

        return false;
    }

    public boolean canCreate(UUID accountId) {
        var user = getAuthenticatedUser();

        return hasAnyRole(user.getRoles(), Role.ROLE_ADMIN) || accountId.equals(user.getId());
    }
}
