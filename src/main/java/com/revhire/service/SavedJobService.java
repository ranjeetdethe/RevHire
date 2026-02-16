package com.revhire.service;

import com.revhire.model.SavedJob;
import java.util.List;

public interface SavedJobService {
    boolean saveJob(int userId, int jobId);

    boolean unsaveJob(int userId, int jobId);

    boolean isJobSaved(int userId, int jobId);

    List<SavedJob> getSavedJobs(int userId);
}
