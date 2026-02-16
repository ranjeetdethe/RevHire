package com.revhire.service;

import com.revhire.model.Resume;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface ResumeService {
    Resume getResumeByUserId(int userId);

    void saveResumeFile(int userId, MultipartFile file) throws IOException;

    // Remaining methods can be kept or refactored later
    // For now we focus on the file upload requirement
}
