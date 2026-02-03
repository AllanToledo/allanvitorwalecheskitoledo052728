package br.dev.allantoledo.psc.dto.file;

import br.dev.allantoledo.psc.entity.File;
import br.dev.allantoledo.psc.service.FileService;
import lombok.Data;

import java.util.UUID;

@Data
public class FileInformation {
    private UUID id;
    private String link;

    public static FileInformation fromCover(File file) {
        FileInformation fileInformation = new FileInformation();
        fileInformation.setId(file.getId());
        String link = String.format("%s/v1/files/%s/%s", FileService.getBaseUrl(), file.getBucket(), file.getName());
        fileInformation.setLink(link);

        return fileInformation;
    }
}

