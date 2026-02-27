import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ApplicationService } from '../../../core/services/application.service';
import { JobService } from '../../../core/services/job.service';
import { ProfileService } from '../../../core/services/profile.service';
import { SavedJobService } from '../../../core/services/saved-job.service';
import { AuthService } from '../../../core/services/auth.service';
import { Application } from '../../../models/application.model';
import { Job } from '../../../models/job.model';
import { User } from '../../../models/user.model';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-seeker-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, StatusBadgeComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  user: User | null = null;
  applications: Application[] = [];
  recommendedJobs: Job[] = [];
  savedJobsCount = 0;
  profileCompleteness = 0;
  isLoading = true;

  constructor(
    private authService: AuthService,
    private appService: ApplicationService,
    private jobService: JobService,
    private profileService: ProfileService,
    private savedJobService: SavedJobService
  ) { }

  ngOnInit() {
    this.user = this.authService.getCurrentUser();
    this.loadDashboardData();
  }

  loadDashboardData() {
    this.isLoading = true;

    // Load Applications
    this.appService.getMyApplications().subscribe(apps => {
      this.applications = apps;
    });

    // Load Profile Completeness
    this.profileService.getProfileCompleteness().subscribe(res => {
      this.profileCompleteness = res?.completeness || 0;
    });

    // Load Saved Jobs
    this.savedJobService.getSavedJobs().subscribe(jobs => {
      this.savedJobsCount = jobs.length;
    });

    // Load Recommended Jobs
    // As JobService does not explicitly have getRecommendedJobs in backend yet 
    // based on our earlier config, we will mock it or fetch latest jobs as recommendations for now
    this.jobService.searchJobs({ size: 5, sort: 'createdAt,desc' }).subscribe(res => {
      this.recommendedJobs = res.content || [];
      this.isLoading = false;
    });
  }

  get recentApplications() {
    // Sort applications by appliedDate descending, taking top 3
    if (!this.applications) return [];
    return [...this.applications]
      .sort((a, b) => new Date(b.appliedAt || 0).getTime() - new Date(a.appliedAt || 0).getTime())
      .slice(0, 3);
  }
}
