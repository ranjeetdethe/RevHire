-- Drop tables if needed for full reset (optional, user might want to keep data but schema changed)
-- DROP TABLE IF EXISTS applications;
-- DROP TABLE IF EXISTS jobs;
-- DROP TABLE IF EXISTS job_seekers;
-- DROP TABLE IF EXISTS employers;
-- DROP TABLE IF EXISTS users;

CREATE DATABASE IF NOT EXISTS revhire_db;
USE revhire_db;

-- 1. Users Table (Enhanced)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role ENUM('SEEKER', 'EMPLOYER') NOT NULL,
    security_question VARCHAR(255),
    security_answer VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Job Seekers Profile (Extension)
CREATE TABLE IF NOT EXISTS job_seekers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE NOT NULL,
    resume_text TEXT,
    education VARCHAR(255),
    experience VARCHAR(255),
    skills VARCHAR(255),
    certifications VARCHAR(255),
    location VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Ensure columns exist if table already exists (Idempotent-ish updates)
ALTER TABLE job_seekers ADD COLUMN education VARCHAR(255);
ALTER TABLE job_seekers ADD COLUMN experience VARCHAR(255);
ALTER TABLE job_seekers ADD COLUMN skills VARCHAR(255);
ALTER TABLE job_seekers ADD COLUMN certifications VARCHAR(255);
ALTER TABLE job_seekers ADD COLUMN location VARCHAR(255);

-- 3. Employers Profile (Extension)
CREATE TABLE IF NOT EXISTS employers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE NOT NULL,
    company_name VARCHAR(100) NOT NULL,
    industry VARCHAR(100),
    location VARCHAR(100),
    description TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 4. Jobs Table
CREATE TABLE IF NOT EXISTS jobs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employer_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(100),
    experience_required INT, 
    salary_range VARCHAR(50), 
    status ENUM('OPEN', 'CLOSED') DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employer_id) REFERENCES employers(id) ON DELETE CASCADE
);

-- 5. Applications Table
CREATE TABLE IF NOT EXISTS applications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    job_id INT NOT NULL,
    seeker_id INT NOT NULL,
    status ENUM('APPLIED', 'SHORTLISTED', 'REJECTED', 'WITHDRAWN') DEFAULT 'APPLIED',
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    FOREIGN KEY (seeker_id) REFERENCES job_seekers(id) ON DELETE CASCADE,
    UNIQUE(job_id, seeker_id) 
);

-- 6. Resumes Table
CREATE TABLE IF NOT EXISTS resumes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    job_seeker_id INT UNIQUE NOT NULL,
    summary TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (job_seeker_id) REFERENCES job_seekers(id) ON DELETE CASCADE
);

-- 7. Resume Education Table
CREATE TABLE IF NOT EXISTS resume_education (
    id INT AUTO_INCREMENT PRIMARY KEY,
    resume_id INT NOT NULL,
    degree VARCHAR(100),
    institution VARCHAR(100),
    year INT,
    grade VARCHAR(20),
    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);

-- 8. Resume Experience Table
CREATE TABLE IF NOT EXISTS resume_experience (
    id INT AUTO_INCREMENT PRIMARY KEY,
    resume_id INT NOT NULL,
    company VARCHAR(100),
    role VARCHAR(100),
    duration VARCHAR(50), -- e.g. "2020-2022" or "2 years"
    description TEXT,
    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);

-- 9. Resume Projects Table
CREATE TABLE IF NOT EXISTS resume_projects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    resume_id INT NOT NULL,
    title VARCHAR(100),
    description TEXT,
    technologies VARCHAR(255),
    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);

-- 10. Resume Skills Table
CREATE TABLE IF NOT EXISTS resume_skills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    resume_id INT NOT NULL,
    skill_name VARCHAR(50),
    proficiency VARCHAR(20), -- Beginner, Intermediate, Expert
    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);
