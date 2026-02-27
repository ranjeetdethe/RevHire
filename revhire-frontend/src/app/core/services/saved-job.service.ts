import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { SavedJob } from '../../models/saved-job.model';

@Injectable({
    providedIn: 'root'
})
export class SavedJobService {
    private apiUrl = `${environment.apiUrl}/saved-jobs`;

    constructor(private http: HttpClient) { }

    getSavedJobs(): Observable<SavedJob[]> {
        return this.http.get<SavedJob[]>(`${this.apiUrl}`);
    }

    saveJob(jobId: number): Observable<any> {
        return this.http.post(`${this.apiUrl}/${jobId}`, {});
    }

    unsaveJob(jobId: number): Observable<any> {
        return this.http.delete(`${this.apiUrl}/${jobId}`);
    }
}
