import { Job } from './job.model';

export interface Application {
    id: number;
    jobId: number;
    jobTitle: string;
    companyName: string;
    coverLetter?: string;
    status: 'APPLIED' | 'UNDER_REVIEW' | 'SHORTLISTED' | 'REJECTED' | 'WITHDRAWN';
    employerNotes?: string;
    withdrawalReason?: string;
    appliedAt: string;
    // Some templates expect `appliedDate` naming — include as optional alias
    appliedDate?: string;
    updatedAt: string;
    // Backend sometimes returns nested job object on the application DTO
    job?: Job;
    // Some templates reference `employerName` on Application — include optional alias
    employerName?: string;
    applicant?: {
        id: number;
        userId: number;
        firstName?: string;
        lastName?: string;
        email?: string;
        resumeUrl?: string; // in case we fetch it later
    }
}
