package br.dev.allantoledo.psc.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name="album")
@EqualsAndHashCode(callSuper = true)
public class Album extends GenericEntity{
    private String name;
}
