package br.dev.allantoledo.psc.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name="artist")
@EqualsAndHashCode(callSuper = true)
public class Artist extends GenericEntity{
    @NotBlank(message = "Nome do artista não pode ser em branco.")
    private String name;
}
