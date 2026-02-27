import { Job } from './job.model';

export interface SavedJob {
    id?: number;
    jobId?: number;
    userId?: number;
    savedAt: string;
    job: Job;
}
