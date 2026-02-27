export interface SeekerProfile {
    id: number;
    userId: number;
    firstName?: string;
    lastName?: string;
    email?: string;
    phone?: string;
    location?: string;
    about?: string;
    headline?: string;
    summary?: string;
    objective?: string;
    resumeUrl?: string;
    resumeFilename?: string;
    education: Education[];
    experience: WorkExperience[];
    skills: Skill[];
    certifications: Certification[];
}

export interface Education {
    id?: number;
    institution: string;
    degree: string;
    fieldOfStudy: string;
    startYear: number;
    endYear?: number;
    isCurrent: boolean;
    description?: string;
}

export interface WorkExperience {
    id?: number;
    companyName: string;
    jobTitle: string;
    description?: string;
    startDate: string;
    endDate?: string;
    isCurrent: boolean;
}

export interface Skill {
    id?: number;
    skillName: string;
    proficiency: 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | 'EXPERT';
}

export interface Certification {
    id?: number;
    name: string;
    issuer: string;
    issueDate?: string;
    expiryDate?: string;
    credentialUrl?: string;
}
