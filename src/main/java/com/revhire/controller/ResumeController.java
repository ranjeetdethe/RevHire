package com.revhire.controller;

import com.revhire.model.Resume;
import com.revhire.service.ResumeService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.revhire.model.User;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("/resume")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @GetMapping("/upload")
    public String showUploadForm(@RequestParam(name = "seekerId", required = false) Integer seekerId,
            HttpSession session,
            Model model) {
        User user = (User) session.getAttribute("user");
        int targetId;

        if (user != null) {
            targetId = user.getId();
        } else if (seekerId != null) {
            targetId = seekerId;
        } else {
            return "redirect:/login";
        }

        model.addAttribute("seekerId", targetId);
        Resume resume = resumeService.getResumeByUserId(targetId);
        if (resume != null) {
            model.addAttribute("resume", resume);
        }
        return "resume-upload";
    }

    @PostMapping("/upload")
    public String uploadResume(@RequestParam(value = "seekerId", required = false) Integer seekerId,
            @RequestParam("file") MultipartFile file,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("user");
        int targetId;

        if (user != null) {
            targetId = user.getId();
        } else if (seekerId != null) {
            targetId = seekerId;
        } else {
            return "redirect:/login";
        }

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/resume/upload?seekerId=" + targetId;
        }

        try {
            resumeService.saveResumeFile(targetId, file);
            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded " + file.getOriginalFilename() + "!");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Failed to upload file");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Error: " + e.getMessage());
        }

        return "redirect:/resume/upload?seekerId=" + targetId;
    }

    @GetMapping("/view/{seekerId}")
    public ResponseEntity<byte[]> getResumeFile(@PathVariable("seekerId") int seekerId) {
        Resume resume = resumeService.getResumeByUserId(seekerId);

        if (resume == null || resume.getData() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resume.getFileName() + "\"")
                .contentType(MediaType.valueOf(resume.getFileType()))
                .body(resume.getData());
    }
}
