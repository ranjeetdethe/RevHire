# RevHire — Implementation Plan & Gap Analysis

## Current State Summary

### ✅ What's Already Built (Working Foundation)
| Layer | Component | Status |
|-------|-----------|--------|
| **Models** | User, JobSeeker, Employer, Job, Application, Resume, ResumeEducation/Experience/Project/Skill, Notification | ✅ Entities defined |
| **Repositories** | UserRepository, JobSeekerRepository, EmployerRepository, JobRepository, ApplicationRepository, ResumeRepository | ✅ Spring Data JPA |
| **Services** | UserService, JobService, ApplicationService, ResumeService | ✅ Interfaces + Impl |
| **Controllers** | AuthController, HomeController, JobController, ApplicationController, EmployerController, JobSeekerController, ResumeController, AdminController | ✅ Basic CRUD |
| **Security** | Spring Security 6 with BCrypt, role-based access (JOB_SEEKER, EMPLOYER, ADMIN), custom success handler, session management | ✅ Configured |
| **DTOs** | UserRegistrationDTO, JobDTO, DashboardStats | ✅ With validation |
| **Templates** | index, login, register, jobs, job-details, fragments, error, employer/dashboard, employer/post-job, seeker/dashboard, admin/dashboard, admin/users, admin/jobs, resume-upload | ✅ Bootstrap 5 |
| **Database** | MySQL schema with 8+ tables, indexes, foreign keys | ✅ Schema defined |

---

## 🔴 Gap Analysis — Missing Features

### Phase 1: Critical Missing Features (High Priority)

#### 1.1 Job Model — Missing Fields
- [ ] `skills` (VARCHAR 255) — required skills for the job
- [ ] `educationRequired` (VARCHAR 100) — education requirement
- [ ] `jobType` (ENUM: FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, REMOTE)
- [ ] `deadline` (DATE) — application deadline
- [ ] `numberOfOpenings` (INT) — number of openings
- [ ] `status` should also include FILLED

#### 1.2 Application Model — Missing Fields
- [ ] `coverLetter` (TEXT) — optional cover letter
- [ ] `withdrawalReason` (VARCHAR 255) — reason for withdrawal
- [ ] `employerNotes` (TEXT) — internal employer notes
- [ ] `UNDER_REVIEW` status missing from ApplicationStatus enum

#### 1.3 Employer Model — Missing Fields
- [ ] `companySize` (VARCHAR 50) — company size
- [ ] `website` (VARCHAR 255) — company website URL

#### 1.4 Notification Model — Not JPA Entity
- [x] **Add Missing Model Fields**
    - [x] `Job.java`: `skills`, `educationRequired`, `jobType`, `deadline`, `numberOfOpenings` (and update `JobStatus` enum).
    - [x] `Application.java`: `coverLetter`, `withdrawalReason`, `employerNotes` (and update `ApplicationStatus` enum).
    - [x] `Employer.java`: `companySize`, `website`.
    - [x] Create `Notification` entity: `userId`, `message`, `createdAt`, `read`.
    - [x] Create `SavedJob` entity: `seekerId`, `jobId`, `savedAt`.

- [x] **Create/Update Repositories**
    - [x] `JobRepository`: Add `advancedSearch(...)` query method.
    - [x] `NotificationRepository`: `findByUserId`, `countUnread`.
    - [x] `SavedJobRepository`: `findBySeekerId`, `existsByJobAndSeeker`, `deleteByJobAndSeeker`.
    - [x] `ApplicationRepository`: `existsByJobAndSeeker`.

#### 2.2 Employer Features — Missing Pages/Endpoints  
- [ ] **View Applicants** — for each job, view list of applicants with profiles & resumes
- [ ] **Shortlist/Reject Applicants** — bulk actions with optional comments
- [ ] **Filter Applicants** — by experience, skills, education, date, status
- [ ] **Edit Job** — edit existing job postings
- [ ] **Close/Reopen Job** — toggle job status
- [ ] **Delete Job** — with confirmation
- [ ] **Mark Job as Filled** — new status
- [ ] **Job Statistics** — view applicant stats per job
- [ ] **Company Profile Management** — view/edit company profile page

#### 2.3 Admin Features — Missing
- [ ] **User Management** — activate/deactivate users
- [ ] **Job Moderation** — approve/remove jobs

---

### Phase 3: Notification System

- [ ] `Notification` entity with JPA
- [ ] `NotificationRepository` 
- [ ] `NotificationService` — create, mark read, get unread count
- [ ] `NotificationController` — REST endpoints for notification bell
- [ ] **Auto-triggers:**
  - Application status change → notify seeker
  - New application received → notify employer
  - Job recommendation → notify seeker (stretch)
- [ ] **Notification bell** in navbar with unread count badge
- [ ] **Notification dropdown/page** showing all notifications

---

### Phase 4: UI/UX Polish & Missing Templates

- [x] **Employer Controller & Views**
    - [x] `viewApplicants` (list applicants for a job).
    - [x] `updateApplicationStatus` (shortlist/reject) - ENFORCED SECURITY CHECK.
    - [x] Create `employer/applicants.html`.
    - [x] `Employer Profile` Management (Edit company info).

- [x] **Job Seeker Controller & Views**
    - [x] `Saved Jobs` endpoints (view, add, remove).
    - [x] `My Applications` endpoint (view list, withdraw).
    - [x] Create `seeker/saved-jobs.html`.
    - [x] Create `my-applications.html`.
    - [x] `Profile` Management (Edit skills, experience, education).

- [x] **Notification Controller & Views**
    - [x] Endpoints to view list and mark as read.
    - [x] Create `notifications.html`.

- [x] **Update Existing Templates**
    - [x] `jobs.html`: Add advanced search filters (Job Type, Experience).
    - [x] `job-details.html`: Display new fields, "Save Job" button, "Apply" modal with cover letter.
    - [x] `fragments.html`: Add "Saved Jobs" link and Notification icon. Updated Navbar with User Dropdown.

- [x] **Integration Tests**
    - [x] Created `JobControllerTest.java`.

### Phase 5: Documentation & Deliverables

- [ ] **ERD Diagram** — Entity Relationship Diagram (can generate from schema)
- [ ] **Architecture Diagram** — Application layered architecture diagram
- [ ] **README.md** — Update with setup instructions, tech stack, screenshots
- [ ] **Testing Artifacts** — Unit tests for services, integration tests

---

## Implementation Order (Recommended)

```
Step 1:  Update Models (Job, Application, Employer, Notification) with missing fields
Step 2:  Create missing entities (SavedJob, Notification as JPA)
Step 3:  Update/Create Repositories
Step 4:  Update/Create Services  
Step 5:  Update/Create Controllers
Step 6:  Build missing Thymeleaf templates
Step 7:  Wire up Notification system
Step 8:  UI polish and testing
Step 9:  Documentation (ERD, Architecture, README)
Step 10: Testing artifacts
```

---

## Tech Stack Confirmation
- **Backend:** Spring Boot 3.2.0, Spring Security 6, Spring Data JPA
- **Frontend:** Thymeleaf, Bootstrap 5.3.2, FontAwesome 6.4
- **Database:** MySQL (port 3307)
- **Build:** Maven, Java 17
- **Auth:** BCrypt + Session-based with Spring Security
