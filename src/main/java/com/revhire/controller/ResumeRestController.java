package com.revhire.controller;

import com.revhire.model.Resume;
import com.revhire.service.ResumeService;
import com.revhire.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/resume")
@SuppressWarnings("null")
public class ResumeRestController {

    @Autowired
    private ResumeService resumeService;

    @PostMapping("/upload")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<?> uploadResume(@RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            resumeService.saveResumeFile(userDetails.getId(), file);
            return ResponseEntity.ok(Map.of("message", "Resume uploaded successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to upload resume"));
        }
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<?> getMyResume(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Resume resume = resumeService.getResumeByUserId(userDetails.getId());
            if (resume != null) {
                // Return basic metadata (we don't strictly need to return fileData in a JSON
                // request unless base64)
                return ResponseEntity.ok(Map.of(
                        "id", resume.getId(),
                        "fileName", resume.getFileName() != null ? resume.getFileName() : "resume",
                        "fileType", resume.getFileType() != null ? resume.getFileType() : "application/pdf",
                        "uploadedAt", resume.getUpdatedAt() != null ? resume.getUpdatedAt()
                                : (resume.getCreatedAt() != null ? resume.getCreatedAt() : "")));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Resume not found"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to retrieve resume metadata"));
        }
    }

    // Main download endpoint acting as /download per prompt
    @GetMapping("/download/{userId}")
    @PreAuthorize("hasAnyRole('JOB_SEEKER', 'EMPLOYER', 'ADMIN')")
    public ResponseEntity<Resource> downloadResume(@PathVariable("userId") int userId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Resume resume = resumeService.getResumeByUserId(userId);
            if (resume != null && resume.getData() != null) {
                byte[] data = resume.getData();
                ByteArrayResource resource = new ByteArrayResource(data);
                String filename = resume.getFileName() != null ? resume.getFileName() : "resume.pdf";
                String fileType = resume.getFileType() != null ? resume.getFileType() : "application/pdf";

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .contentLength(data.length)
                        .contentType(MediaType.parseMediaType(fileType))
                        .body(resource);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // View resume inline (browser view)
    @GetMapping("/view/{id}")
    @PreAuthorize("hasAnyRole('JOB_SEEKER', 'EMPLOYER', 'ADMIN')")
    public ResponseEntity<byte[]> viewResume(@PathVariable("id") int id) {
        try {
            // Assume id is the job seeker's user ID for this context OR the resume ID.
            Resume resume = resumeService.getResumeByUserId(id);
            if (resume != null && resume.getData() != null) {
                byte[] data = resume.getData();
                String filename = resume.getFileName() != null ? resume.getFileName() : "resume.pdf";
                String fileType = resume.getFileType() != null ? resume.getFileType() : "application/pdf";

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .contentType(MediaType.parseMediaType(fileType))
                        .body(data);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<?> deleteResume(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            boolean deleted = resumeService.deleteResume(userDetails.getId());
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Resume deleted successfully"));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to delete resume"));
        }
    }
}
