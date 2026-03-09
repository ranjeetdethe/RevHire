import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { JobService } from '../../core/services/job.service';
import { Job } from '../../models/job.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {
  jobCategories = [
    { name: 'Tech', icon: 'bi-code-slash', count: '1.2K Jobs', colorClass: 'cat-tech' },
    { name: 'Healthcare', icon: 'bi-heart-pulse-fill', count: '890 Jobs', colorClass: 'cat-health' },
    { name: 'Sales & Marketing', icon: 'bi-bar-chart-fill', count: '950 Jobs', colorClass: 'cat-sales' },
    { name: 'Finance', icon: 'bi-currency-dollar', count: '610 Jobs', colorClass: 'cat-finance' },
    { name: 'Customer Service', icon: 'bi-headset', count: '770 Jobs', colorClass: 'cat-service' },
    { name: 'HR', icon: 'bi-people-fill', count: '430 Jobs', colorClass: 'cat-hr' },
    { name: 'Engineering', icon: 'bi-gear-wide-connected', count: '520 Jobs', colorClass: 'cat-engineering' },
    { name: 'Design', icon: 'bi-palette-fill', count: '380 Jobs', colorClass: 'cat-design' }
  ];

  featuredJobs: Job[] = [];

  topCompanies = ['amazon', 'tata', 'google', 'ibm', 'nike', 'samsung', 'man', 'sharson'];

  constructor(private jobService: JobService, private router: Router) { }

  ngOnInit() {
    this.jobService.searchJobs({ size: 4, sort: 'createdAt,desc' }).subscribe({
      next: (res) => {
        this.featuredJobs = res.content || [];
      },
      error: () => {
        this.featuredJobs = [];
      }
    });
  }

  navigateToJob(jobId: number) {
    this.router.navigate(['/job-seeker/search-jobs', jobId]);
  }
}
