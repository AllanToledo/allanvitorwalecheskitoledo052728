package br.dev.allantoledo.psc.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name="artist")
@EqualsAndHashCode(callSuper = true)
public class Artist extends GenericEntity{
    private String name;
}
