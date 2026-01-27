package com.revhire.dao;

import com.revhire.model.Application;
import java.util.List;
import java.util.Optional;

public interface ApplicationDAO {
    Application apply(Application application);

    Optional<Application> findById(int id);

    List<Application> findByJobId(int jobId);

    List<Application> findBySeekerId(int seekerId);

    boolean updateStatus(int applicationId, Application.ApplicationStatus status);

    int countByJobAndStatus(int jobId, Application.ApplicationStatus status);

}
