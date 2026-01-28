package br.dev.allantoledo.psc.dto.user;

import lombok.Data;

import java.util.Optional;

@Data
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType"})
public class SecureUserUpdateForm {
    private Optional<String> name;
    private Optional<String> email;
    private Optional<String> password;
}
