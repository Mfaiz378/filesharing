package main.java.com.securefiles.repository;

import com.securefiles.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findByUploadedByUserUsername(String username);
}
