import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { Employer } from '../../models/employer.model';

@Injectable({
    providedIn: 'root'
})
export class EmployerService {
    private apiUrl = `${environment.apiUrl}/employer`;

    constructor(private http: HttpClient) { }

    getCompanyProfile(): Observable<Employer> {
        return this.http.get<Employer>(`${this.apiUrl}/company`);
    }

    updateCompanyProfile(companyData: Partial<Employer>): Observable<Employer> {
        return this.http.put<Employer>(`${this.apiUrl}/company`, companyData);
    }
}
