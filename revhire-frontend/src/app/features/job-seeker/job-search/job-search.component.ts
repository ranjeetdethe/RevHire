import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { JobService } from '../../../core/services/job.service';
import { SavedJobService } from '../../../core/services/saved-job.service';
import { Job, JobSearchParams } from '../../../models/job.model';
import { ToastComponent } from '../../../shared/components/toast/toast.component';

@Component({
  selector: 'app-job-search',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, ToastComponent],
  templateUrl: './job-search.component.html',
  styleUrl: './job-search.component.scss'
})
export class JobSearchComponent implements OnInit {
  jobs: Job[] = [];
  savedJobIds: Set<number> = new Set();

  isLoading = true;
  isSaving: { [key: number]: boolean } = {};

  totalElements = 0;
  totalPages = 0;
  currentPage = 0;

  // Search parameters
  searchParams: JobSearchParams = {
    page: 0,
    size: 10,
    sort: 'createdAt,desc'
  };

  filters = {
    keyword: '',
    location: '',
    jobType: '',
    experience: ''
  };

  // Toast
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';
  showToast = false;

  constructor(
    private jobService: JobService,
    private savedJobService: SavedJobService
  ) { }

  ngOnInit() {
    this.loadJobs();
    this.loadSavedJobIds();
  }

  loadJobs() {
    this.isLoading = true;

    // Map UI filters to API params
    this.searchParams.title = this.filters.keyword ? this.filters.keyword : undefined;
    this.searchParams.location = this.filters.location ? this.filters.location : undefined;
    this.searchParams.jobType = this.filters.jobType ? this.filters.jobType : undefined;

    if (this.filters.experience) {
      this.searchParams.experienceMin = parseInt(this.filters.experience, 10);
    } else {
      this.searchParams.experienceMin = undefined;
    }

    this.jobService.searchJobs(this.searchParams).subscribe({
      next: (response) => {
        this.jobs = response.content;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.isLoading = false;

        // Ensure UI doesn't visually break if page is out of bounds
        window.scrollTo({ top: 0, behavior: 'smooth' });
      },
      error: (err) => {
        console.error('Failed to load jobs', err);
        this.jobs = [];
        this.isLoading = false;
        this.displayToast('Failed to load jobs. Please try again.', 'error');
      }
    });
  }

  loadSavedJobIds() {
    this.savedJobService.getSavedJobs().subscribe({
      next: (savedJobs) => {
        savedJobs.forEach(sj => {
          if (sj.job && sj.job.id) {
            this.savedJobIds.add(sj.job.id);
          }
        });
      },
      error: () => { }
    });
  }

  onSearch() {
    this.searchParams.page = 0;
    this.currentPage = 0;
    this.loadJobs();
  }

  clearFilters() {
    this.filters = { keyword: '', location: '', jobType: '', experience: '' };
    this.onSearch();
  }

  toggleSaveJob(jobId: number, event: Event) {
    event.preventDefault();
    event.stopPropagation();

    if (this.isSaving[jobId]) return;

    this.isSaving[jobId] = true;

    if (this.savedJobIds.has(jobId)) {
      // Technically backend needs savedJob id not jobId to delete, but assuming 
      // the user manages removal fully through saved-jobs tab for simplicity
      // or we could implement a check here later. 
      // Let's just focus on saving for now.
      this.displayToast('Job is already saved. Go to Saved Jobs to manage.', 'error');
      this.isSaving[jobId] = false;
    } else {
      this.savedJobService.saveJob(jobId).subscribe({
        next: () => {
          this.savedJobIds.add(jobId);
          this.displayToast('Job saved successfully!');
          this.isSaving[jobId] = false;
        },
        error: () => {
          this.displayToast('Failed to save job.', 'error');
          this.isSaving[jobId] = false;
        }
      });
    }
  }

  changePage(delta: number) {
    if ((delta < 0 && this.currentPage === 0) || (delta > 0 && this.currentPage >= this.totalPages - 1)) {
      return;
    }
    this.currentPage += delta;
    this.searchParams.page = this.currentPage;
    this.loadJobs();
  }

  displayToast(msg: string, type: 'success' | 'error' = 'success') {
    this.toastMessage = msg;
    this.toastType = type;
    this.showToast = true;
    setTimeout(() => this.showToast = false, 3000);
  }

  parseSkills(skillsText: string): string[] {
    if (!skillsText) return [];
    return skillsText.split(',').map(s => s.trim()).filter(s => s.length > 0).slice(0, 4); // Limit to 4 tags
  }
}
