package br.dev.allantoledo.psc.dto.user;

import lombok.Data;

import java.util.Collection;

@Data
public class UserCollection {
    private Collection<UserInformation> users;
}
