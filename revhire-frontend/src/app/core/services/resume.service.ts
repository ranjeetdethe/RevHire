import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class ResumeService {
    private apiUrl = `${environment.apiUrl}/resume`;

    constructor(private http: HttpClient) { }

    uploadResume(file: File): Observable<any> {
        const formData = new FormData();
        formData.append('file', file);
        return this.http.post(`${this.apiUrl}/upload`, formData);
    }

    getMyResume(): Observable<any> {
        return this.http.get(`${this.apiUrl}/my`);
    }

    downloadResume(userId: number): Observable<Blob> {
        return this.http.get(`${this.apiUrl}/download/${userId}`, { responseType: 'blob' });
    }

    deleteResume(): Observable<any> {
        return this.http.delete(`${this.apiUrl}`);
    }
}
