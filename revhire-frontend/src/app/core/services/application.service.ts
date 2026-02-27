import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Application } from '../../models/application.model';
import { PagedResponse } from '../../models/job.model';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class ApplicationService {
    private apiUrl = `${environment.apiUrl}/applications`;

    constructor(private http: HttpClient) { }

    applyForJob(jobId: number, coverLetter?: string): Observable<Application> {
        return this.http.post<Application>(`${this.apiUrl}/apply/${jobId}`, { coverLetter });
    }

    getMyApplications(): Observable<Application[]> {
        return this.http.get<Application[]>(`${this.apiUrl}/my`);
    }

    checkApplicationStatus(jobId: number): Observable<{ hasApplied: boolean }> {
        return this.http.get<{ hasApplied: boolean }>(`${this.apiUrl}/check/${jobId}`);
    }

    getJobApplications(jobId: number, filters?: any): Observable<PagedResponse<Application>> {
        let params = new HttpParams();
        if (filters) {
            if (filters.status) params = params.set('status', filters.status);
            if (filters.page !== undefined) params = params.set('page', filters.page.toString());
            if (filters.size !== undefined) params = params.set('size', filters.size.toString());
        }
        return this.http.get<PagedResponse<Application>>(`${this.apiUrl}/job/${jobId}`, { params });
    }

    updateStatus(id: number, status: string, notes?: string): Observable<Application> {
        return this.http.put<Application>(`${this.apiUrl}/${id}/status`, { status, notes });
    }

    withdrawApplication(id: number, reason?: string): Observable<Application> {
        return this.http.put<Application>(`${this.apiUrl}/${id}/status`, { status: 'WITHDRAWN', notes: reason });
    }

    addNotes(id: number, notes: string): Observable<Application> {
        return this.http.put<Application>(`${this.apiUrl}/${id}/notes`, { notes });
    }

    bulkUpdateStatus(ids: number[], status: string): Observable<any> {
        return this.http.patch(`${this.apiUrl}/bulk-status`, { ids, status });
    }
}
