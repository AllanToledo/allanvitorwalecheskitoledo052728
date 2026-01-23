package br.dev.allantoledo.psc.dto;

import lombok.Data;

@Data
public class UserCreationForm {
    private String name;
    private String email;
    private String password;
}
