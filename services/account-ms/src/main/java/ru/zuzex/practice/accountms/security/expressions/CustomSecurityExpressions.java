package ru.zuzex.practice.accountms.security.expressions;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.zuzex.practice.accountms.security.JwtUser;
import ru.zuzex.practice.accountms.security.Role;

import java.util.Set;
import java.util.UUID;

@Component("customSecurityExpression")
public class CustomSecurityExpressions {

    public boolean hasPermission(UUID id) {
        var user = getAuthenticatedUser();
        return user.getId().equals(id) || hasAnyRole(user.getRoles(), Role.ROLE_ADMIN);
    }

    public boolean isOwner(UUID id) {
        var user = getAuthenticatedUser();
        var userId = user.getId();

        return userId.equals(id);
    }

    private JwtUser getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (JwtUser) authentication.getPrincipal();
    }

    private boolean hasAnyRole(Set<Role> curUserRoles, Role... roles) {
        for (Role role : roles) {
            System.out.println(role.name());
            if (curUserRoles.contains(role))
                return true;
        }
        return false;
    }
}
