package br.dev.allantoledo.psc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="regional")
public class RegionalEndpoint extends GenericEntity {
    @Column(name="id_extern")
    private Long idExtern;
    private String name;
    private Boolean active;
}
