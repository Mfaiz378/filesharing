package main.java.com.securefiles.controller;

import com.securefiles.model.FileEntity;
import com.securefiles.model.FileRequest;
import com.securefiles.model.User;
import com.securefiles.service.Mailservice;
import com.securefiles.service.SmsService;
import com.securefiles.repository.FileRepository;
import com.securefiles.repository.FileRequestRepository;
import com.securefiles.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/files")
@CrossOrigin
public class FileController {

    @Autowired
    private Mailservice mailservice;

    @Autowired
    private SmsService smsService;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileRequestRepository fileRequestRepository;

    private final String uploadDir = "uploads/";

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
            @RequestParam("username") String username) throws IOException {
        User uploader = userRepository.findByUsername(username).orElseThrow();
        Path path = Paths.get(uploadDir + file.getOriginalFilename());
        Files.createDirectories(path.getParent());
        file.transferTo(new File(path.toString()));

        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(file.getOriginalFilename());
        fileEntity.setFilePath(path.toString());
        fileEntity.setUploadedByUser(uploader);
        fileEntity.setFileSize(file.getSize());
        fileEntity.setContentType(file.getContentType());
        fileRepository.save(fileEntity);

        return ResponseEntity.ok("File uploaded successfully");
    }

    @GetMapping("/user/{username}")
    public List<FileEntity> listUserFiles(@PathVariable String username) {
        return fileRepository.findByUploadedByUserUsername(username);
    }

    @PostMapping("/request-access")
    public ResponseEntity<?> requestFileAccess(@RequestParam Long fileId, @RequestParam String requesterUsername) {
        User requester = userRepository.findByUsername(requesterUsername).orElseThrow();
        FileEntity fileEntity = fileRepository.findById(fileId).orElseThrow();

        // Save file access request
        FileRequest request = new FileRequest();
        request.setRequesterUser(requester);
        request.setFileEntity(fileEntity);
        fileRequestRepository.save(request);

        // Notify file owner via email
        User fileOwner = fileEntity.getUploadedByUser();
        String ownerEmail = fileOwner.getEmail();
        mailService.sendEmail(ownerEmail,
                "File Access Requested",
                "User " + requester.getUsername() + " is requesting access to your file: " + fileEntity.getFileName());

        // Notify file owner via SMS
        smsService.sendSms(fileOwner.getPhoneNumber(),
                "User " + requester.getUsername() + " is requesting access to file: " + fileEntity.getFileName());

        return ResponseEntity.ok("Request sent successfully and file owner notified!");
    }

    @GetMapping("/requests-for-user/{username}")
    public List<FileRequest> requestsForUser(@PathVariable String username) {
        return fileRequestRepository.findByFileEntityUploadedByUserUsername(username);
    }

    @PostMapping("/respond-request")
    public ResponseEntity<?> respondRequest(@RequestParam Long requestId, @RequestParam boolean approved) {
        FileRequest fileRequest = fileRequestRepository.findById(requestId).orElseThrow();
        fileRequest.setStatus(approved ? FileRequest.Status.APPROVED : FileRequest.Status.REJECTED);
        fileRequestRepository.save(fileRequest);

        return ResponseEntity.ok(approved ? "Request approved" : "Request rejected");
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long fileId) throws IOException {
        FileEntity fileEntity = fileRepository.findById(fileId).orElseThrow();
        Path path = Paths.get(fileEntity.getFilePath());
        byte[] fileBytes = Files.readAllBytes(path);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + fileEntity.getFileName())
                .body(fileBytes);
    }
}
