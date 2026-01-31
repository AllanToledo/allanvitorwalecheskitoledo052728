package br.dev.allantoledo.psc.controller;

import br.dev.allantoledo.psc.entity.File;
import br.dev.allantoledo.psc.service.FileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@Tag(name = "Arquivos")
public class FileController {

    private final FileService fileService;

    @GetMapping("/v1files/{bucket}/{name}")
    public ResponseEntity<InputStreamResource> getFile(
            @PathVariable String bucket,
            @PathVariable String name
    ) {
        File file = fileService.getFileByBucketAndName(bucket, name);
        InputStream fileStream = fileService.getFileStream(file);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(file.getMediaType()));
        headers.setContentDisposition(
            ContentDisposition
                .builder("inline")
                .filename(file.getName())
                .build()
        );

        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(fileStream));
    }

}
