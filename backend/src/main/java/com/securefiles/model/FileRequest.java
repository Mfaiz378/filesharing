package com.securefiles.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "file_requests")
public class FileRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requester_user_id")
    private User requesterUser;

    @ManyToOne
    @JoinColumn(name = "file_id")
    private FileEntity fileEntity;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    public enum Status {
        PENDING, APPROVED, REJECTED
    }
}
