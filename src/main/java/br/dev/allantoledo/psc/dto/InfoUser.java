package br.dev.allantoledo.psc.dto;

import br.dev.allantoledo.psc.entity.User;
import lombok.Data;

import java.util.Optional;
import java.util.UUID;

@Data
public class InfoUser {
    private UUID id;
    private String name;
    private String email;
    private Boolean isAdmin;

    public static InfoUser fromUser(User user) {
        InfoUser infoUser = new InfoUser();
        infoUser.setId(user.getId());
        infoUser.setName(user.getName());
        infoUser.setEmail(user.getEmail());
        infoUser.setIsAdmin(user.getIsAdmin());
        return infoUser;
    }
}
