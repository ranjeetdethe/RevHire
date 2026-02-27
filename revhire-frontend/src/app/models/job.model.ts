export interface Job {
    id: number;
    title: string;
    description: string;
    requiredSkills: string;
    experienceYearsMin: number;
    experienceYearsMax?: number;
    educationRequired?: string;
    location: string;
    salaryMin?: number;
    salaryMax?: number;
    // Backwards-compatible aliases used in templates
    minSalary?: number;
    maxSalary?: number;
    jobType: 'FULL_TIME' | 'PART_TIME' | 'CONTRACT' | 'INTERNSHIP' | 'REMOTE';
    status: 'OPEN' | 'CLOSED' | 'FILLED' | 'DRAFT';
    deadline?: string;
    openingsCount: number;
    viewsCount: number;
    companyName: string;
    // Some templates reference `employerName` instead of `companyName`
    employerName?: string;
    companyLogo?: string;
    postedBy: number;
    postedByUserId?: number;
    createdAt: string;
}

export interface JobSearchParams {
    title?: string;
    location?: string;
    jobType?: string;
    experienceMin?: number;
    salaryMin?: number;
    salaryMax?: number;
    companyName?: string;
    datePosted?: string;
    page?: number;
    size?: number;
    sort?: string;
}

export interface PagedResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    number: number;
    size: number;
}
