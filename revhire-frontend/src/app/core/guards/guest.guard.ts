import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const guestGuard: CanActivateFn = () => {
    const authService = inject(AuthService);
    const router = inject(Router);

    if (authService.isLoggedIn()) {
        if (authService.isEmployer()) {
            router.navigate(['/employer/dashboard']);
        } else if (authService.isJobSeeker()) {
            router.navigate(['/job-seeker/dashboard']);
        } else {
            router.navigate(['/auth/login']);
        }
        return false;
    }

    // Not logged in, allow guest access (e.g., to login/register)
    return true;
};
