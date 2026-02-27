import { Injectable, PLATFORM_ID, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { environment } from '../../../environments/environment';
import { AuthResponse, RegisterRequest, User } from '../../models/user.model';
import { BehaviorSubject, tap, Observable, map } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private apiUrl = `${environment.apiUrl}/auth`;

    private currentUserSubject = new BehaviorSubject<User | null>(this.getUserFromStorage());
    public currentUser$ = this.currentUserSubject.asObservable();

    private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasToken());
    public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

    constructor(
        private http: HttpClient,
        private router: Router,
        @Inject(PLATFORM_ID) private platformId: Object
    ) { }

    private isBrowser(): boolean {
        return isPlatformBrowser(this.platformId);
    }

    private getUserFromStorage(): User | null {
        if (!this.isBrowser()) return null;
        const user = localStorage.getItem('revhire_user');
        return user ? JSON.parse(user) as User : null;
    }

    private hasToken(): boolean {
        if (!this.isBrowser()) return false;
        return !!localStorage.getItem('revhire_token');
    }

    login(email: string, password: string, role?: string): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.apiUrl}/login`, { email, password, role }).pipe(
            map(res => {
                // Handle different possible backend response structures safely
                const data = (res as any).data ? (res as any).data : res;
                return {
                    token: data.token,
                    user: {
                        id: data.id || data.user?.id || data.userId,
                        email: data.email || data.user?.email,
                        role: data.role || data.user?.role,
                        firstName: data.firstName || data.user?.firstName,
                        lastName: data.lastName || data.user?.lastName
                    }
                } as AuthResponse;
            }),
            tap(res => {
                if (res.token && this.isBrowser()) {
                    localStorage.setItem('revhire_token', res.token);
                    localStorage.setItem('revhire_role', res.user.role || '');
                    localStorage.setItem('revhire_user', JSON.stringify(res.user));
                    this.isAuthenticatedSubject.next(true);
                    this.currentUserSubject.next(res.user);
                }
            })
        );
    }

    register(req: RegisterRequest): Observable<any> {
        return this.http.post<any>(`${this.apiUrl}/register`, req);
    }

    logout(): void {
        if (this.isBrowser()) {
            localStorage.removeItem('revhire_token');
            localStorage.removeItem('token'); // User requested this specifically
            localStorage.clear();
            sessionStorage.clear();
        }
        this.currentUserSubject.next(null);
        this.isAuthenticatedSubject.next(false);
        this.router.navigate(['/auth/login']);
    }

    isLoggedIn(): boolean {
        return this.isAuthenticatedSubject.value;
    }

    getRole(): string | null {
        if (!this.isBrowser()) return null;
        return localStorage.getItem('revhire_role');
    }

    getCurrentUser(): User | null {
        return this.currentUserSubject.value;
    }

    isEmployer(): boolean {
        return this.getRole() === 'EMPLOYER';
    }

    isJobSeeker(): boolean {
        return this.getRole() === 'JOB_SEEKER';
    }

    getMe(): Observable<User> {
        return this.http.get<User>(`${this.apiUrl}/me`).pipe(
            tap(user => {
                if (this.isBrowser()) {
                    localStorage.setItem('revhire_user', JSON.stringify(user));
                    localStorage.setItem('revhire_role', user.role || '');
                }
                this.currentUserSubject.next(user);
            })
        );
    }

    changePassword(req: any) {
        return this.http.put(`${this.apiUrl}/change-password`, req);
    }
}
