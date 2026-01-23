package br.dev.allantoledo.psc.dto;

import br.dev.allantoledo.psc.entity.User;
import lombok.Data;

import java.util.UUID;

@Data
public class UserInformation {
    private UUID id;
    private String name;
    private String email;
    private Boolean isAdmin;

    public static UserInformation fromUser(User user) {
        UserInformation userInformation = new UserInformation();
        userInformation.setId(user.getId());
        userInformation.setName(user.getName());
        userInformation.setEmail(user.getEmail());
        userInformation.setIsAdmin(user.getIsAdmin());
        return userInformation;
    }
}
