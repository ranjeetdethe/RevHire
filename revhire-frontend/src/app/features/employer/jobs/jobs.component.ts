import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { JobService } from '../../../core/services/job.service';
import { ApplicationService } from '../../../core/services/application.service';
import { AuthService } from '../../../core/services/auth.service';
import { Job } from '../../../models/job.model';
import { User } from '../../../models/user.model';
import { ToastComponent } from '../../../shared/components/toast/toast.component';

@Component({
  selector: 'app-employer-jobs',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ToastComponent],
  templateUrl: './jobs.component.html',
  styleUrl: './jobs.component.scss'
})
export class JobsComponent implements OnInit {
  user: User | null = null;
  myJobs: Job[] = [];
  filteredJobs: Job[] = [];

  totalJobs = 0;
  openJobsCount = 0;
  closedJobsCount = 0;

  isLoading = true;
  isDeleting: { [key: number]: boolean } = {};
  isStatusChanging: { [key: number]: boolean } = {};

  // Modals
  showDeleteModal = false;
  jobToDelete: Job | null = null;

  // Filters
  searchTerm = '';
  statusFilter = 'ALL';

  // Toast
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';
  showToast = false;

  constructor(
    private jobService: JobService,
    private authService: AuthService
  ) { }

  ngOnInit() {
    this.user = this.authService.getCurrentUser();

    if (this.user && this.user.id) {
      this.loadMyJobs(this.user.id);
    } else {
      this.authService.currentUser$.subscribe(data => {
        if (data && data.id) {
          this.user = data;
          this.loadMyJobs(data.id);
        }
      });
    }
  }

  loadMyJobs(userId: number) {
    this.isLoading = true;

    this.jobService.getMyJobs().subscribe({
      next: (res) => {
        // Enforce descending order by creation date natively
        this.myJobs = res.content.sort((a, b) => new Date(b.createdAt || 0).getTime() - new Date(a.createdAt || 0).getTime());
        this.applyFilters();
        this.isLoading = false;
      },
      error: () => {
        this.myJobs = [];
        this.filteredJobs = [];
        this.isLoading = false;
        this.displayToast('Failed to load your jobs. Please try again.', 'error');
      }
    });
  }

  onSearchChange() {
    this.applyFilters();
  }

  onFilterChange(status: string) {
    this.statusFilter = status;
    this.applyFilters();
  }

  applyFilters() {
    this.totalJobs = this.myJobs.length;
    this.openJobsCount = this.myJobs.filter(j => j.status === 'OPEN').length;
    this.closedJobsCount = this.myJobs.filter(j => j.status === 'CLOSED').length;

    this.filteredJobs = this.myJobs.filter(job => {
      const titleMatch = job.title?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        job.location?.toLowerCase().includes(this.searchTerm.toLowerCase());

      const statusMatch = this.statusFilter === 'ALL' || job.status === this.statusFilter;

      return (this.searchTerm ? titleMatch : true) && statusMatch;
    });
  }

  toggleJobStatus(job: Job) {
    if (this.isStatusChanging[job.id]) return;

    const newStatus = job.status === 'OPEN' ? 'CLOSED' : 'OPEN';
    this.isStatusChanging[job.id] = true;

    // Note: Backend might require a specific status update payload or PUT/PATCH
    this.jobService.updateJobStatus(job.id, newStatus).subscribe({
      next: () => {
        job.status = newStatus;
        this.isStatusChanging[job.id] = false;
        this.applyFilters(); // Re-apply filters in case they are filtered out
        this.displayToast(`Job status changed to ${newStatus}`);
      },
      error: (err) => {
        this.isStatusChanging[job.id] = false;
        this.displayToast(err.error?.message || 'Failed to update job status.', 'error');
      }
    });
  }

  confirmDelete(job: Job) {
    this.jobToDelete = job;
    this.showDeleteModal = true;
  }

  closeDeleteModal() {
    this.showDeleteModal = false;
    this.jobToDelete = null;
  }

  deleteJob() {
    if (!this.jobToDelete) return;

    const id = this.jobToDelete.id;
    this.isDeleting[id] = true;

    this.jobService.deleteJob(id).subscribe({
      next: () => {
        this.myJobs = this.myJobs.filter(j => j.id !== id);
        this.applyFilters();
        this.isDeleting[id] = false;
        this.showDeleteModal = false;
        this.jobToDelete = null;
        this.displayToast('Job posting deleted successfully.');
      },
      error: (err) => {
        this.isDeleting[id] = false;
        this.showDeleteModal = false;
        this.jobToDelete = null;
        this.displayToast(err.error?.message || 'Failed to delete job posting.', 'error');
      }
    });
  }

  displayToast(msg: string, type: 'success' | 'error' = 'success') {
    this.toastMessage = msg;
    this.toastType = type;
    this.showToast = true;
    setTimeout(() => this.showToast = false, 3000);
  }
}
