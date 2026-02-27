export interface User {
    id: number;
    email: string;
    role: 'JOB_SEEKER' | 'EMPLOYER';
    firstName: string;
    lastName: string;
    phone?: string;
    location?: string;
    employmentStatus?: string;
}

export interface AuthResponse {
    token: string;
    user: User;
}

export interface LoginRequest {
    email: string;
    password: string;
}

export interface RegisterRequest {
    email: string;
    password: string;
    role: string;
    firstName: string;
    lastName: string;
    phone?: string;
    location?: string;
    employmentStatus?: string;
    companyName?: string;
    companyIndustry?: string;
    companySize?: string;
    companyLocation?: string;
}
