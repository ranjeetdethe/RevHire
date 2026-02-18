package com.revhire.controller;

import com.revhire.model.Resume;
import com.revhire.security.CustomUserDetails;
import com.revhire.service.ResumeService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/resume")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @GetMapping("/upload")
    public String showUploadForm(@AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        int userId = userDetails.getId();
        model.addAttribute("seekerId", userId);

        Resume resume = resumeService.getResumeByUserId(userId);
        if (resume != null) {
            model.addAttribute("resume", resume);
        }

        return "resume-upload";
    }

    @PostMapping("/upload")
    public String uploadResume(@AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        int userId = userDetails.getId();

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/resume/upload";
        }

        try {
            // Check file size (redundant if configured in properties, but good for UX
            // message)
            if (file.getSize() > 5 * 1024 * 1024) {
                redirectAttributes.addFlashAttribute("message", "File is too large. Max size is 5MB.");
                return "redirect:/resume/upload";
            }

            // Check file type
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.equals("application/pdf") &&
                    !contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") &&
                    !contentType.equals("application/msword"))) {
                redirectAttributes.addFlashAttribute("message",
                        "Invalid file type. Please upload PDF or Word document.");
                return "redirect:/resume/upload";
            }

            resumeService.saveResumeFile(userId, file);
            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded " + file.getOriginalFilename() + "!");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Failed to upload file: IO Error");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "An unexpected error occurred: " + e.getMessage());
        }

        return "redirect:/resume/upload";
    }

    @GetMapping("/view/{seekerId}")
    public ResponseEntity<byte[]> getResumeFile(@PathVariable("seekerId") int seekerId) {
        Resume resume = resumeService.getResumeByUserId(seekerId);

        if (resume == null || resume.getData() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resume.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(resume.getFileType()))
                .body(resume.getData());
    }
}
