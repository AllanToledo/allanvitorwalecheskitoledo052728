package br.dev.allantoledo.psc.dto;

import lombok.Data;

import java.util.Optional;

@Data
public class UpdatedUser {
    private Optional<String> name;
    private Optional<String> email;
    private Optional<String> password;
    private Optional<Boolean> isAdmin;
}
