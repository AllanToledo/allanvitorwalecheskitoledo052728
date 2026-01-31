package br.dev.allantoledo.psc.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "file")
@EqualsAndHashCode(callSuper = true)
public class File extends GenericEntity{
    private String bucket;
    private String name;
    private String mediaType;
    private Long size;
}
