package br.dev.allantoledo.psc.dto.user;

import lombok.Data;

import java.util.Optional;

@Data
public class UserUpdateForm {
    private Optional<String> name;
    private Optional<String> email;
    private Optional<Boolean> isAdmin;
    private Optional<String> password;
}
