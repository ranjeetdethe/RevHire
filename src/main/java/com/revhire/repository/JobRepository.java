package com.revhire.repository;

import com.revhire.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Integer> {
    @org.springframework.data.jpa.repository.Query("SELECT j FROM Job j JOIN FETCH j.employer WHERE j.status = :status")
    List<Job> findByStatusWithEmployer(Job.JobStatus status);

    List<Job> findByStatus(Job.JobStatus status);

    List<Job> findByEmployer_Id(int employerId);

    List<Job> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);

    @org.springframework.data.jpa.repository.Query("SELECT j FROM Job j JOIN FETCH j.employer WHERE (LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')) AND j.status = 'OPEN'")
    List<Job> searchJobs(@org.springframework.data.repository.query.Param("keyword") String keyword,
            @org.springframework.data.repository.query.Param("location") String location);

    @org.springframework.data.jpa.repository.Query("SELECT j FROM Job j JOIN FETCH j.employer WHERE " +
            "(:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
            +
            "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:jobType IS NULL OR j.jobType = :jobType) AND " +
            "(:experience IS NULL OR j.experienceRequired <= :experience) AND " +
            "j.status = 'OPEN'")
    List<Job> advancedSearch(
            @org.springframework.data.repository.query.Param("keyword") String keyword,
            @org.springframework.data.repository.query.Param("location") String location,
            @org.springframework.data.repository.query.Param("jobType") Job.JobType jobType,
            @org.springframework.data.repository.query.Param("experience") Integer experience);
}
