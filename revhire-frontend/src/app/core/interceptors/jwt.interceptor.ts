import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
    const router = inject(Router);

    // Skip attaching token for login and register routes
    if (req.url.includes('/api/auth/login') || req.url.includes('/api/auth/register')) {
        return next(req);
    }

    // Get token from local storage
    const token = localStorage.getItem('revhire_token');

    // Clone request and attach token if it exists
    if (token) {
        req = req.clone({
            setHeaders: { Authorization: `Bearer ${token}` }
        });
    }

    // Handle response errors
    return next(req).pipe(
        catchError((error: HttpErrorResponse) => {
            if (error.status === 401) {
                // Clear storage and redirect on unauthorized
                localStorage.removeItem('revhire_token');
                localStorage.removeItem('revhire_role');
                router.navigate(['/auth/login']);
            }
            return throwError(() => error);
        })
    );
};
