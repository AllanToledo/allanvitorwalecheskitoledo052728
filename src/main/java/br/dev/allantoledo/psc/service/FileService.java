package br.dev.allantoledo.psc.service;

import br.dev.allantoledo.psc.entity.File;
import br.dev.allantoledo.psc.repository.FileRepository;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Log
@Service
@RequiredArgsConstructor
public class FileService {

    private MinioClient client;
    private final FileRepository fileRepository;

    @PostConstruct
    public void init() {
        client = MinioClient.builder()
            .endpoint("http://127.0.0.1:9000")
            .credentials("minioadmin", "minioadmin")
            .build();
    }

    public File save(File file, MultipartFile data) throws IOException {
        file = fileRepository.save(file);
        saveFile(file.getBucket(), file.getName(), data.getInputStream(), data.getSize());
        return file;
    }

    private void saveFile(String bucket, String name, InputStream input, long objectSize) {
        try {
            client.putObject(
                PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(name)
                    .stream(input, objectSize, 50 * 1024 * 1024)
                    .build()
            );
        } catch (Exception e) {
            log.severe(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public File getFileByBucketAndName(String bucket, String name) {
        return fileRepository.getFileByBucketAndName(bucket, name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public InputStream getFileStream(File file) {
        try {
            return client.getObject(
                GetObjectArgs.builder()
                    .bucket(file.getBucket())
                    .object(file.getName())
                    .build()
            );
        } catch (Exception e) {
            log.severe(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void delete(UUID id) {
        File file = fileRepository.findById(id).orElse(null);
        if (file == null) return;

        fileRepository.delete(file);
        try {
            client.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(file.getBucket())
                    .object(file.getName())
                    .build()
            );
        } catch (Exception e) {
            log.severe(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
