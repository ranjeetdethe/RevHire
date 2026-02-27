import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Job, JobSearchParams, PagedResponse } from '../../models/job.model';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class JobService {
    private apiUrl = `${environment.apiUrl}/jobs`;

    constructor(private http: HttpClient) { }

    searchJobs(params: JobSearchParams): Observable<PagedResponse<Job>> {
        let httpParams = new HttpParams();
        if (params.title) httpParams = httpParams.set('title', params.title);
        if (params.location) httpParams = httpParams.set('location', params.location);
        if (params.jobType) httpParams = httpParams.set('jobType', params.jobType);
        if (params.experienceMin) httpParams = httpParams.set('experienceMin', params.experienceMin.toString());
        if (params.salaryMin) httpParams = httpParams.set('salaryMin', params.salaryMin.toString());
        if (params.salaryMax) httpParams = httpParams.set('salaryMax', params.salaryMax.toString());
        if (params.companyName) httpParams = httpParams.set('companyName', params.companyName);
        if (params.datePosted) httpParams = httpParams.set('datePosted', params.datePosted);
        if (params.page !== undefined) httpParams = httpParams.set('page', params.page.toString());
        if (params.size !== undefined) httpParams = httpParams.set('size', params.size.toString());
        if (params.sort) httpParams = httpParams.set('sort', params.sort);

        return this.http.get<PagedResponse<Job>>(`${this.apiUrl}`, { params: httpParams });
    }

    getMyJobs(): Observable<PagedResponse<Job>> {
        return this.http.get<PagedResponse<Job>>(`${this.apiUrl}/my`);
    }

    getJobById(id: number): Observable<Job> {
        return this.http.get<Job>(`${this.apiUrl}/${id}`);
    }

    createJob(job: Partial<Job>): Observable<Job> {
        return this.http.post<Job>(`${this.apiUrl}`, job);
    }

    updateJob(id: number, job: Partial<Job>): Observable<Job> {
        return this.http.put<Job>(`${this.apiUrl}/${id}`, job);
    }

    deleteJob(id: number): Observable<any> {
        return this.http.delete(`${this.apiUrl}/${id}`);
    }

    updateJobStatus(id: number, status: string): Observable<Job> {
        return this.http.patch<Job>(`${this.apiUrl}/${id}/status`, { status });
    }

    getJobStats(id: number): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}/${id}/stats`);
    }
}
