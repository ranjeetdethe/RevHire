package com.revhire.repository;

import com.revhire.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Integer> {
    Optional<Resume> findByJobSeekerId(int jobSeekerId);
}
