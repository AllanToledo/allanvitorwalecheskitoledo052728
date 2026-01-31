package br.dev.allantoledo.psc.repository;

import br.dev.allantoledo.psc.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface FileRepository extends JpaRepository<File, UUID> {
    @Query("SELECT f FROM File f WHERE f.bucket = :bucket AND f.name = :name")
    Optional<File> getFileByBucketAndName(String bucket, String name);
}
