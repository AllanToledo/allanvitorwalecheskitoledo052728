package br.dev.allantoledo.psc.dto.album;

import lombok.Data;

import java.util.List;

@Data
public class AlbumCollection {
    private List<AlbumInformation> albums;
}
