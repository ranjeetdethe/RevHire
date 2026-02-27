import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const employerGuard: CanActivateFn = () => {
    const authService = inject(AuthService);
    const router = inject(Router);

    if (authService.isLoggedIn() && authService.isEmployer()) {
        return true;
    }

    // Not an employer, redirect to unauthorized
    router.navigate(['/auth/unauthorized']);
    return false;
};
