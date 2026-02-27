-- Create Database
CREATE DATABASE IF NOT EXISTS revhire;
USE revhire;

-- Note: MySQL doesn't use CREATE TYPE ... AS ENUM. We define ENUMs inside the column declarations.

-- USERS table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('JOB_SEEKER', 'EMPLOYER') NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    location VARCHAR(255),
    employment_status ENUM('EMPLOYED', 'UNEMPLOYED', 'FREELANCER', 'STUDENT'),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_email (email),
    INDEX idx_users_role (role)
);

-- COMPANIES table
CREATE TABLE IF NOT EXISTS companies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    industry VARCHAR(100),
    size VARCHAR(50),
    description TEXT,
    website VARCHAR(255),
    location VARCHAR(255),
    logo_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- EMPLOYER_USERS (links employers to companies)
CREATE TABLE IF NOT EXISTS employer_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    is_admin BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY (user_id, company_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
);

-- SEEKER_PROFILES table
CREATE TABLE IF NOT EXISTS seeker_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    headline VARCHAR(255),
    summary TEXT,
    objective TEXT,
    resume_url VARCHAR(500),
    resume_filename VARCHAR(255),
    resume_uploaded_at TIMESTAMP NULL DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_seeker_profiles_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- EDUCATIONS table
CREATE TABLE IF NOT EXISTS educations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    seeker_profile_id BIGINT NOT NULL,
    institution VARCHAR(255) NOT NULL,
    degree VARCHAR(100),
    field_of_study VARCHAR(100),
    start_year INT,
    end_year INT,
    is_current BOOLEAN DEFAULT FALSE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (seeker_profile_id) REFERENCES seeker_profiles(id) ON DELETE CASCADE
);

-- WORK_EXPERIENCES table
CREATE TABLE IF NOT EXISTS work_experiences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    seeker_profile_id BIGINT NOT NULL,
    company_name VARCHAR(255) NOT NULL,
    job_title VARCHAR(255) NOT NULL,
    description TEXT,
    start_date DATE,
    end_date DATE,
    is_current BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (seeker_profile_id) REFERENCES seeker_profiles(id) ON DELETE CASCADE
);

-- SKILLS table
CREATE TABLE IF NOT EXISTS skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    seeker_profile_id BIGINT NOT NULL,
    skill_name VARCHAR(100) NOT NULL,
    proficiency ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT') DEFAULT 'INTERMEDIATE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (seeker_profile_id) REFERENCES seeker_profiles(id) ON DELETE CASCADE
);

-- CERTIFICATIONS table
CREATE TABLE IF NOT EXISTS certifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    seeker_profile_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    issuer VARCHAR(255),
    issue_date DATE,
    expiry_date DATE,
    credential_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (seeker_profile_id) REFERENCES seeker_profiles(id) ON DELETE CASCADE
);

-- JOBS table
CREATE TABLE IF NOT EXISTS jobs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id BIGINT NOT NULL,
    posted_by BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    required_skills TEXT,
    experience_years_min INT DEFAULT 0,
    experience_years_max INT,
    education_required VARCHAR(100),
    location VARCHAR(255),
    salary_min DECIMAL(12,2),
    salary_max DECIMAL(12,2),
    job_type ENUM('FULL_TIME', 'PART_TIME', 'CONTRACT', 'INTERNSHIP', 'REMOTE') DEFAULT 'FULL_TIME',
    status ENUM('OPEN', 'CLOSED', 'FILLED', 'DRAFT') DEFAULT 'OPEN',
    deadline DATE,
    openings_count INT DEFAULT 1,
    views_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_jobs_company_id (company_id),
    INDEX idx_jobs_status (status),
    INDEX idx_jobs_created_at (created_at),
    INDEX idx_jobs_location (location),
    INDEX idx_jobs_job_type (job_type),
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    FOREIGN KEY (posted_by) REFERENCES users(id)
);

-- APPLICATIONS table
CREATE TABLE IF NOT EXISTS applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id BIGINT NOT NULL,
    seeker_profile_id BIGINT NOT NULL,
    cover_letter TEXT,
    status ENUM('APPLIED', 'UNDER_REVIEW', 'SHORTLISTED', 'REJECTED', 'WITHDRAWN') DEFAULT 'APPLIED',
    employer_notes TEXT,
    withdrawal_reason TEXT,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY (job_id, seeker_profile_id),
    INDEX idx_applications_job_id (job_id),
    INDEX idx_applications_seeker_profile_id (seeker_profile_id),
    INDEX idx_applications_status (status),
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    FOREIGN KEY (seeker_profile_id) REFERENCES seeker_profiles(id) ON DELETE CASCADE
);

-- SAVED_JOBS table
CREATE TABLE IF NOT EXISTS saved_jobs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    seeker_profile_id BIGINT NOT NULL,
    job_id BIGINT NOT NULL,
    saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY (seeker_profile_id, job_id),
    FOREIGN KEY (seeker_profile_id) REFERENCES seeker_profiles(id) ON DELETE CASCADE,
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE
);

-- NOTIFICATIONS table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type ENUM('APPLICATION_UPDATE', 'JOB_RECOMMENDATION', 'NEW_APPLICATION', 'GENERAL') DEFAULT 'GENERAL',
    is_read BOOLEAN DEFAULT FALSE,
    reference_id BIGINT,
    reference_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_notifications_user_id (user_id),
    INDEX idx_notifications_is_read (user_id, is_read),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Seed data for testing
INSERT IGNORE INTO users (email, password_hash, role, first_name, last_name, phone, location, employment_status)
VALUES 
('seeker@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'JOB_SEEKER', 'John', 'Doe', '9876543210', 'Pune', 'EMPLOYED'),
('employer@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'EMPLOYER', 'Jane', 'Smith', '9123456780', 'Mumbai', NULL);
-- Password for both: password123

INSERT IGNORE INTO companies (name, industry, size, description, website, location)
VALUES ('TechCorp India', 'Information Technology', '500-1000', 'Leading IT company', 'https://techcorp.com', 'Mumbai');

-- Assumes user_id=2 and user_id=1 matches the inserts directly above if DB was completely empty
-- INSERT IGNORE INTO employer_users (user_id, company_id, is_admin) VALUES (2, 1, TRUE);
-- INSERT IGNORE INTO seeker_profiles (user_id, headline, summary) VALUES (1, 'Full Stack Developer', 'Experienced developer with 3 years in Java and Angular');
