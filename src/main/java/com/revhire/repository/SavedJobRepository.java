package com.revhire.repository;

import com.revhire.model.SavedJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJob, Integer> {
    List<SavedJob> findByJobSeeker_Id(int seekerId);

    Optional<SavedJob> findByJobSeeker_IdAndJob_Id(int seekerId, int jobId);

    boolean existsByJobSeeker_IdAndJob_Id(int seekerId, int jobId);
}
