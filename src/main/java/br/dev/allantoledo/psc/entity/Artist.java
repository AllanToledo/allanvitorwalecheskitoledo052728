package br.dev.allantoledo.psc.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name="artist")
@EqualsAndHashCode(callSuper = true)
public class Artist extends GenericEntity{
    @NotBlank(message = "Nome do artista não pode ser em branco.")
    private String name;

    @ManyToMany(mappedBy = "authors", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Album> albums = new HashSet<>();
}
