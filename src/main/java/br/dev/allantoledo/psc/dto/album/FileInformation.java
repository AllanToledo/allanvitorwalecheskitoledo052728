package br.dev.allantoledo.psc.dto.album;

import br.dev.allantoledo.psc.entity.File;
import lombok.Data;

import java.util.UUID;

@Data
public class FileInformation {
    private UUID id;
    private String link;

    public static FileInformation fromCover(File file) {
        FileInformation fileInformation = new FileInformation();
        fileInformation.setId(file.getId());
        String link = String.format("http://localhost:8080/files/%s/%s", file.getBucket(), file.getName());
        fileInformation.setLink(link);

        return fileInformation;
    }
}

