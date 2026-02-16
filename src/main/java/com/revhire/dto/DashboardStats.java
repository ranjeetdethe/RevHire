package com.revhire.dto;

/**
 * DTO for Dashboard Statistics
 * Provides pre-calculated counts to avoid complex logic in Thymeleaf templates
 */
public class DashboardStats {

    private long totalApplications;
    private long appliedCount;
    private long shortlistedCount;
    private long rejectedCount;
    private long withdrawnCount;
    private long totalJobs;
    private long activeJobs;
    private long closedJobs;

    // Constructor
    public DashboardStats() {
    }

    // Getters and Setters
    public long getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(long totalApplications) {
        this.totalApplications = totalApplications;
    }

    public long getAppliedCount() {
        return appliedCount;
    }

    public void setAppliedCount(long appliedCount) {
        this.appliedCount = appliedCount;
    }

    public long getShortlistedCount() {
        return shortlistedCount;
    }

    public void setShortlistedCount(long shortlistedCount) {
        this.shortlistedCount = shortlistedCount;
    }

    public long getRejectedCount() {
        return rejectedCount;
    }

    public void setRejectedCount(long rejectedCount) {
        this.rejectedCount = rejectedCount;
    }

    public long getWithdrawnCount() {
        return withdrawnCount;
    }

    public void setWithdrawnCount(long withdrawnCount) {
        this.withdrawnCount = withdrawnCount;
    }

    public long getTotalJobs() {
        return totalJobs;
    }

    public void setTotalJobs(long totalJobs) {
        this.totalJobs = totalJobs;
    }

    public long getActiveJobs() {
        return activeJobs;
    }

    public void setActiveJobs(long activeJobs) {
        this.activeJobs = activeJobs;
    }

    public long getClosedJobs() {
        return closedJobs;
    }

    public void setClosedJobs(long closedJobs) {
        this.closedJobs = closedJobs;
    }

    // Convenience method for "in review" count (applied status)
    public long getInReviewCount() {
        return appliedCount;
    }
}
