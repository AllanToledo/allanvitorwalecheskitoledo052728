package br.dev.allantoledo.psc.dto.artist;

import lombok.Data;

import java.util.Optional;

@Data
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class ArtistUpdateForm {
    private Optional<String> name;
}
