package br.dev.allantoledo.psc.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name="app_user")
@EqualsAndHashCode(callSuper = true)
public class User extends GenericEntity {
    @NotBlank(message = "Nome do usuário não pode ser em branco.")
    private String name;
    @NotBlank(message = "Email do usuário não pode estar em branco.")
    private String email;
    @NotNull(message = "Senha do usuário não pode ser nula.")
    private String password;
    @NotNull(message = "É preciso definir se usuário é administrador.")
    private Boolean isAdmin;
}
