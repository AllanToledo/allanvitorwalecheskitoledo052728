package br.dev.allantoledo.psc.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name="album")
@EqualsAndHashCode(callSuper = true)
public class Album extends GenericEntity{
    @NotBlank(message = "Nome do álbum não pode ser em branco.")
    private String name;
}
