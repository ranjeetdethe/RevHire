package com.revhire.repository;

import com.revhire.model.JobSeeker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobSeekerRepository extends JpaRepository<JobSeeker, Integer> {
    Optional<JobSeeker> findByUser_Id(int userId);
}
