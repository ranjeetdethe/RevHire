import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SavedJobService } from '../../../core/services/saved-job.service';
import { SavedJob } from '../../../models/saved-job.model';
import { ToastComponent } from '../../../shared/components/toast/toast.component';

@Component({
  selector: 'app-saved-jobs',
  standalone: true,
  imports: [CommonModule, RouterModule, ToastComponent],
  templateUrl: './saved-jobs.component.html',
  styleUrl: './saved-jobs.component.scss'
})
export class SavedJobsComponent implements OnInit {
  savedJobs: SavedJob[] = [];
  isLoading = true;
  // Use string index so we can safely index by expressions that may be undefined at template compile time
  isRemoving: { [key: string]: boolean } = {};

  // Toast
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';
  showToast = false;

  constructor(private savedJobService: SavedJobService) { }

  ngOnInit() {
    this.loadSavedJobs();
  }

  loadSavedJobs() {
    this.isLoading = true;
    this.savedJobService.getSavedJobs().subscribe({
      next: (jobs) => {
        // Sort by savedAt descending
        this.savedJobs = jobs.sort((a, b) => new Date(b.savedAt || 0).getTime() - new Date(a.savedAt || 0).getTime());
        this.isLoading = false;
      },
      error: () => {
        this.savedJobs = [];
        this.isLoading = false;
        this.displayToast('Failed to load saved jobs', 'error');
      }
    });
  }

  removeSavedJob(jobId: number | undefined, event?: Event) {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }
    const key = String(jobId ?? '');
    if (!jobId || this.isRemoving[key]) return;

    this.isRemoving[key] = true;

    this.savedJobService.unsaveJob(jobId).subscribe({
      next: () => {
        this.savedJobs = this.savedJobs.filter(sj => sj.job?.id !== jobId && sj.jobId !== jobId);
        this.isRemoving[key] = false;
        this.displayToast('Job removed from saved list');
      },
      error: () => {
        this.isRemoving[key] = false;
        this.displayToast('Failed to remove job', 'error');
      }
    });
  }

  getKey(item: SavedJob): string {
    const id = item.job?.id ?? item.jobId;
    return id != null ? String(id) : '';
  }

  getSalaryLabel(item: SavedJob): string {
    const min = item.job?.salaryMin;
    const max = item.job?.salaryMax;
    if (!min && !max) return '';
    if (min && !max) return '$' + (min / 1000) + 'k';
    if (!min && max) return '$' + (max / 1000) + 'k';
    return '$' + (min! / 1000) + 'k - $' + (max! / 1000) + 'k';
  }

  displayToast(msg: string, type: 'success' | 'error' = 'success') {
    this.toastMessage = msg;
    this.toastType = type;
    this.showToast = true;
    setTimeout(() => this.showToast = false, 3000);
  }
}
