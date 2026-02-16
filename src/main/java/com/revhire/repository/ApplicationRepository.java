package com.revhire.repository;

import com.revhire.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    List<Application> findByJob_Id(int jobId);

    List<Application> findByJobSeeker_User_Id(int userId);

    int countByJob_IdAndStatus(int jobId, Application.ApplicationStatus status);

    boolean existsByJob_IdAndJobSeeker_Id(int jobId, int seekerId);
}
