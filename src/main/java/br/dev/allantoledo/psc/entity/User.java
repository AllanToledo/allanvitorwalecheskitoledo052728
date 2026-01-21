package br.dev.allantoledo.psc.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@Entity
@Table(name="app_user")
@EqualsAndHashCode(callSuper = true)
public class User extends GenericEntity {
    private String name;
    private String email;
    private String password;
}
