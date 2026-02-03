package br.dev.allantoledo.psc.dto.file;

import br.dev.allantoledo.psc.entity.File;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

@Data
public class FileInformation {
    private UUID id;
    private String link;

    @Value("${application.baseUrl}")
    private static String baseUrl;
    public static FileInformation fromCover(File file) {
        FileInformation fileInformation = new FileInformation();
        fileInformation.setId(file.getId());
        String link = String.format("%s/v1/files/%s/%s", baseUrl, file.getBucket(), file.getName());
        fileInformation.setLink(link);

        return fileInformation;
    }
}

