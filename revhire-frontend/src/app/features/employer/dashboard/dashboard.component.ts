import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { JobService } from '../../../core/services/job.service';
import { ApplicationService } from '../../../core/services/application.service';
import { Job } from '../../../models/job.model';
import { AuthService } from '../../../core/services/auth.service';
import { User } from '../../../models/user.model';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-employer-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, StatusBadgeComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  user: User | null = null;
  myJobs: Job[] = [];

  isLoading = true;
  totalJobs = 0;
  activeJobs = 0;
  totalApplicationsReceived = 0; // Using a mock value or aggregated data if possible

  constructor(
    private jobService: JobService,
    private applicationService: ApplicationService,
    private authService: AuthService
  ) { }

  ngOnInit() {
    this.user = this.authService.getCurrentUser();

    if (this.user && this.user.id) {
      this.loadDashboardData(this.user.id);
    } else {
      // fallback if currentUser() is empty but behavior subject isn't
      this.authService.currentUser$.subscribe(data => {
        if (data && data.id) {
          this.user = data;
          this.loadDashboardData(data.id);
        }
      });
    }
  }

  loadDashboardData(userId: number) {
    this.isLoading = true;

    // In a real application, backend would have a `/api/v1/employer/me/jobs` or `?postedBy=userId`
    // We will use searchJobs mapping to grab latest jobs natively, and do client-side filtering if backend doesn't support
    this.jobService.getMyJobs().subscribe({
      next: (res) => {
        // Enforce descending order by creation date
        this.myJobs = res.content.sort((a, b) => new Date(b.createdAt || 0).getTime() - new Date(a.createdAt || 0).getTime());

        this.totalJobs = this.myJobs.length;
        this.activeJobs = this.myJobs.filter(j => j.status === 'OPEN').length;

        // Mocking total applications across all jobs for now to show dashboard UI layout metrics.
        // E.g sum of viewsCount or random multiplier, unless we pull each job's applications via API loop
        // Let's use viewsCount as a proxy for 'Engagement' for UI purposes
        this.totalApplicationsReceived = this.myJobs.reduce((sum, job) => sum + (job.viewsCount || 0), 0);

        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  get recentJobs() {
    return [...this.myJobs].slice(0, 5); // Take top 5 for recent
  }
}
