import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { employerGuard } from './core/guards/employer.guard';
import { jobSeekerGuard } from './core/guards/job-seeker.guard';
import { guestGuard } from './core/guards/guest.guard';

export const routes: Routes = [
    {
        path: '',
        loadComponent: () => import('./pages/home/home.component').then(m => m.HomeComponent)
    },
    {
        path: 'resources/resume-guide',
        loadComponent: () => import('./pages/resources/resume-guide/resume-guide.component').then(m => m.ResumeGuideComponent)
    },
    {
        path: 'resources/interview-tips',
        loadComponent: () => import('./pages/resources/interview-tips/interview-tips.component').then(m => m.InterviewTipsComponent)
    },
    {
        path: 'resources/salary-negotiation',
        loadComponent: () => import('./pages/resources/salary-negotiation/salary-negotiation.component').then(m => m.SalaryNegotiationComponent)
    },
    {
        path: 'auth',
        children: [
            {
                path: 'login',
                canActivate: [guestGuard],
                loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
            },
            {
                path: 'register',
                canActivate: [guestGuard],
                loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent)
            },
            {
                path: 'unauthorized',
                loadComponent: () => import('./features/auth/unauthorized/unauthorized.component').then(m => m.UnauthorizedComponent)
            }
        ]
    },
    {
        path: 'employer',
        canActivate: [authGuard, employerGuard],
        loadComponent: () => import('./layouts/employer-layout/employer-layout.component').then(m => m.EmployerLayoutComponent),
        children: [
            {
                path: 'dashboard',
                loadComponent: () => import('./features/employer/dashboard/dashboard.component').then(m => m.DashboardComponent)
            },
            {
                path: 'jobs',
                loadComponent: () => import('./features/employer/jobs/jobs.component').then(m => m.JobsComponent)
            },
            {
                path: 'job-form',
                loadComponent: () => import('./features/employer/job-form/job-form.component').then(m => m.JobFormComponent)
            },
            {
                path: 'job-form/:id',
                loadComponent: () => import('./features/employer/job-form/job-form.component').then(m => m.JobFormComponent)
            },
            {
                path: 'applicants/:jobId',
                loadComponent: () => import('./features/employer/applicants/applicants.component').then(m => m.ApplicantsComponent)
            },
            {
                path: 'company',
                loadComponent: () => import('./features/employer/company/company.component').then(m => m.CompanyComponent)
            },
            {
                path: 'notifications',
                loadComponent: () => import('./features/employer/notifications/notifications.component').then(m => m.NotificationsComponent)
            },
            { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
        ]
    },
    {
        path: 'job-seeker',
        loadComponent: () => import('./layouts/dashboard-layout/dashboard-layout.component').then(m => m.DashboardLayoutComponent),
        canActivate: [authGuard, jobSeekerGuard],
        children: [
            {
                path: 'dashboard',
                loadComponent: () => import('./features/job-seeker/dashboard/dashboard.component').then(m => m.DashboardComponent)
            },
            {
                path: 'search-jobs',
                loadComponent: () => import('./features/job-seeker/job-search/job-search.component').then(m => m.JobSearchComponent)
            },
            {
                path: 'search-jobs/:id',
                loadComponent: () => import('./features/job-seeker/job-detail/job-detail.component').then(m => m.JobDetailComponent)
            },
            {
                path: 'profile',
                loadComponent: () => import('./features/job-seeker/profile/profile.component').then(m => m.ProfileComponent)
            },
            {
                path: 'my-applications',
                loadComponent: () => import('./features/job-seeker/applications/applications.component').then(m => m.ApplicationsComponent)
            },
            {
                path: 'resume',
                loadComponent: () => import('./features/job-seeker/resume/resume.component').then(m => m.ResumeComponent)
            },
            {
                path: 'saved-jobs',
                loadComponent: () => import('./features/job-seeker/saved-jobs/saved-jobs.component').then(m => m.SavedJobsComponent)
            },
            {
                path: 'notifications',
                loadComponent: () => import('./features/job-seeker/notifications/notifications.component').then(m => m.NotificationsComponent)
            },
            { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
        ]
    },
    { path: '**', redirectTo: '/auth/login' }
];
