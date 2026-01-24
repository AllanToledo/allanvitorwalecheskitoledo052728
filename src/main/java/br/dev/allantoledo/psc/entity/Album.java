package br.dev.allantoledo.psc.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@Entity
@Table(name="album")
@EqualsAndHashCode(callSuper = true)
public class Album extends GenericEntity{
    @NotBlank(message = "Nome do álbum não pode ser em branco.")
    private String name;
    @NotNull(message = "Ano do álbum não pode ser nulo.")
    private Integer year;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "author",
            joinColumns = @JoinColumn(name = "id_album"),
            inverseJoinColumns = @JoinColumn(name = "id_artist")
    )
    private Set<Artist> authors;
}
