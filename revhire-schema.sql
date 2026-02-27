-- Create Database
-- CREATE DATABASE revhire;
-- \c revhire;

-- ENUM Types
CREATE TYPE user_role AS ENUM ('JOB_SEEKER', 'EMPLOYER');
CREATE TYPE employment_status AS ENUM ('EMPLOYED', 'UNEMPLOYED', 'FREELANCER', 'STUDENT');
CREATE TYPE job_type AS ENUM ('FULL_TIME', 'PART_TIME', 'CONTRACT', 'INTERNSHIP', 'REMOTE');
CREATE TYPE job_status AS ENUM ('OPEN', 'CLOSED', 'FILLED', 'DRAFT');
CREATE TYPE application_status AS ENUM ('APPLIED', 'UNDER_REVIEW', 'SHORTLISTED', 'REJECTED', 'WITHDRAWN');
CREATE TYPE proficiency_level AS ENUM ('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT');
CREATE TYPE notification_type AS ENUM ('APPLICATION_UPDATE', 'JOB_RECOMMENDATION', 'NEW_APPLICATION', 'GENERAL');

-- USERS table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role user_role NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    location VARCHAR(255),
    employment_status employment_status,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- COMPANIES table
CREATE TABLE companies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    industry VARCHAR(100),
    size VARCHAR(50),
    description TEXT,
    website VARCHAR(255),
    location VARCHAR(255),
    logo_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- EMPLOYER_USERS (links employers to companies)
CREATE TABLE employer_users (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    company_id BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    is_admin BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, company_id)
);

-- SEEKER_PROFILES table
CREATE TABLE seeker_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    headline VARCHAR(255),
    summary TEXT,
    objective TEXT,
    resume_url VARCHAR(500),
    resume_filename VARCHAR(255),
    resume_uploaded_at TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_seeker_profiles_user_id ON seeker_profiles(user_id);

-- EDUCATIONS table
CREATE TABLE educations (
    id BIGSERIAL PRIMARY KEY,
    seeker_profile_id BIGINT NOT NULL REFERENCES seeker_profiles(id) ON DELETE CASCADE,
    institution VARCHAR(255) NOT NULL,
    degree VARCHAR(100),
    field_of_study VARCHAR(100),
    start_year INT,
    end_year INT,
    is_current BOOLEAN DEFAULT FALSE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- WORK_EXPERIENCES table
CREATE TABLE work_experiences (
    id BIGSERIAL PRIMARY KEY,
    seeker_profile_id BIGINT NOT NULL REFERENCES seeker_profiles(id) ON DELETE CASCADE,
    company_name VARCHAR(255) NOT NULL,
    job_title VARCHAR(255) NOT NULL,
    description TEXT,
    start_date DATE,
    end_date DATE,
    is_current BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- SKILLS table
CREATE TABLE skills (
    id BIGSERIAL PRIMARY KEY,
    seeker_profile_id BIGINT NOT NULL REFERENCES seeker_profiles(id) ON DELETE CASCADE,
    skill_name VARCHAR(100) NOT NULL,
    proficiency proficiency_level DEFAULT 'INTERMEDIATE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- CERTIFICATIONS table
CREATE TABLE certifications (
    id BIGSERIAL PRIMARY KEY,
    seeker_profile_id BIGINT NOT NULL REFERENCES seeker_profiles(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    issuer VARCHAR(255),
    issue_date DATE,
    expiry_date DATE,
    credential_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- JOBS table
CREATE TABLE jobs (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    posted_by BIGINT NOT NULL REFERENCES users(id),
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    required_skills TEXT,
    experience_years_min INT DEFAULT 0,
    experience_years_max INT,
    education_required VARCHAR(100),
    location VARCHAR(255),
    salary_min NUMERIC(12,2),
    salary_max NUMERIC(12,2),
    job_type job_type DEFAULT 'FULL_TIME',
    status job_status DEFAULT 'OPEN',
    deadline DATE,
    openings_count INT DEFAULT 1,
    views_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_jobs_company_id ON jobs(company_id);
CREATE INDEX idx_jobs_status ON jobs(status);
CREATE INDEX idx_jobs_created_at ON jobs(created_at DESC);
CREATE INDEX idx_jobs_location ON jobs(location);
CREATE INDEX idx_jobs_job_type ON jobs(job_type);

-- APPLICATIONS table
CREATE TABLE applications (
    id BIGSERIAL PRIMARY KEY,
    job_id BIGINT NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    seeker_profile_id BIGINT NOT NULL REFERENCES seeker_profiles(id) ON DELETE CASCADE,
    cover_letter TEXT,
    status application_status DEFAULT 'APPLIED',
    employer_notes TEXT,
    withdrawal_reason TEXT,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(job_id, seeker_profile_id)
);
CREATE INDEX idx_applications_job_id ON applications(job_id);
CREATE INDEX idx_applications_seeker_profile_id ON applications(seeker_profile_id);
CREATE INDEX idx_applications_status ON applications(status);

-- SAVED_JOBS table
CREATE TABLE saved_jobs (
    id BIGSERIAL PRIMARY KEY,
    seeker_profile_id BIGINT NOT NULL REFERENCES seeker_profiles(id) ON DELETE CASCADE,
    job_id BIGINT NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(seeker_profile_id, job_id)
);

-- NOTIFICATIONS table
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type notification_type DEFAULT 'GENERAL',
    is_read BOOLEAN DEFAULT FALSE,
    reference_id BIGINT,
    reference_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(user_id, is_read);

-- Seed data for testing
INSERT INTO users (email, password_hash, role, first_name, last_name, phone, location, employment_status)
VALUES 
('seeker@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'JOB_SEEKER', 'John', 'Doe', '9876543210', 'Pune', 'EMPLOYED'),
('employer@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'EMPLOYER', 'Jane', 'Smith', '9123456780', 'Mumbai', NULL);
-- Password for both: password123

INSERT INTO companies (name, industry, size, description, website, location)
VALUES ('TechCorp India', 'Information Technology', '500-1000', 'Leading IT company', 'https://techcorp.com', 'Mumbai');

INSERT INTO employer_users (user_id, company_id, is_admin)
VALUES (2, 1, TRUE);

INSERT INTO seeker_profiles (user_id, headline, summary)
VALUES (1, 'Full Stack Developer', 'Experienced developer with 3 years in Java and Angular');
