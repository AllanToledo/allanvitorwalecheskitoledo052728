package br.dev.allantoledo.psc.dto;

import lombok.Data;

import java.util.Optional;

@Data
public class ArtistUpdateForm {
    private Optional<String> name;
}
