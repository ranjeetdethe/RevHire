import { User } from './user.model';

export interface Employer {
    id?: number;
    user?: User;
    companyName: string;
    industry?: string;
    location?: string;
    description?: string;
    companySize?: string;
    website?: string;
}
