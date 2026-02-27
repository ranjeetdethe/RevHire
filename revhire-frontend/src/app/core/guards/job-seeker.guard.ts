import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const jobSeekerGuard: CanActivateFn = () => {
    const authService = inject(AuthService);
    const router = inject(Router);

    if (authService.isLoggedIn() && authService.isJobSeeker()) {
        return true;
    }

    // Not a job seeker, redirect to unauthorized
    router.navigate(['/auth/unauthorized']);
    return false;
};
