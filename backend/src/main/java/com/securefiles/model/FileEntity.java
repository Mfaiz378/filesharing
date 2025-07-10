package main.java.com.securefiles.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "files")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "uploaded_by_user_id")
    private User uploadedByUser;

    private Long fileSize;

    private String contentType;
}
