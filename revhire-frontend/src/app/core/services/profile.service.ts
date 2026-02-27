import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { SeekerProfile, Education, WorkExperience, Skill, Certification } from '../../models/profile.model';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class ProfileService {
    // Base seeker API. Individual methods append /profile, /education, /skills, etc.
    // This aligns with backend SeekerProfileRestController which is mapped to /api/v1/seeker.
    private apiUrl = `${environment.apiUrl}/seeker`;

    constructor(private http: HttpClient) { }

    getProfile(): Observable<SeekerProfile> {
        return this.http.get<SeekerProfile>(`${this.apiUrl}/profile`);
    }

    getProfileCompleteness(): Observable<{ completeness: number }> {
        return this.http.get<{ completeness: number }>(`${this.apiUrl}/profile/completeness`);
    }

    updateProfile(data: Partial<SeekerProfile>): Observable<SeekerProfile> {
        return this.http.put<SeekerProfile>(`${this.apiUrl}/profile`, data);
    }

    addEducation(edu: Education): Observable<Education> {
        return this.http.post<Education>(`${this.apiUrl}/education`, edu);
    }

    updateEducation(id: number, edu: Education): Observable<Education> {
        return this.http.put<Education>(`${this.apiUrl}/education/${id}`, edu);
    }

    deleteEducation(id: number): Observable<any> {
        return this.http.delete(`${this.apiUrl}/education/${id}`);
    }

    addExperience(exp: WorkExperience): Observable<WorkExperience> {
        return this.http.post<WorkExperience>(`${this.apiUrl}/experience`, exp);
    }

    updateExperience(id: number, exp: WorkExperience): Observable<WorkExperience> {
        return this.http.put<WorkExperience>(`${this.apiUrl}/experience/${id}`, exp);
    }

    deleteExperience(id: number): Observable<any> {
        return this.http.delete(`${this.apiUrl}/experience/${id}`);
    }

    addSkill(skill: Skill): Observable<Skill> {
        return this.http.post<Skill>(`${this.apiUrl}/skills`, skill);
    }

    deleteSkill(id: number): Observable<any> {
        return this.http.delete(`${this.apiUrl}/skills/${id}`);
    }

    addCertification(cert: Certification): Observable<Certification> {
        return this.http.post<Certification>(`${this.apiUrl}/certifications`, cert);
    }

    updateCertification(id: number, cert: Certification): Observable<Certification> {
        return this.http.put<Certification>(`${this.apiUrl}/certifications/${id}`, cert);
    }

    deleteCertification(id: number): Observable<any> {
        return this.http.delete(`${this.apiUrl}/certifications/${id}`);
    }
}
