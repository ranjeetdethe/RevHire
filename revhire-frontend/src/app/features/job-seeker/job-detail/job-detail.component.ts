import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { JobService } from '../../../core/services/job.service';
import { ApplicationService } from '../../../core/services/application.service';
import { SavedJobService } from '../../../core/services/saved-job.service';
import { Job } from '../../../models/job.model';
import { ToastComponent } from '../../../shared/components/toast/toast.component';

@Component({
  selector: 'app-job-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ToastComponent],
  templateUrl: './job-detail.component.html',
  styleUrl: './job-detail.component.scss'
})
export class JobDetailComponent implements OnInit {
  job: Job | null = null;
  isLoading = true;
  error = '';

  // Application State
  hasApplied = false;
  isApplying = false;
  showApplyModal = false;
  coverLetter = '';

  // Saved Job State
  isSaved = false;
  isSaving = false;

  // Toast
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';
  showToast = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private jobService: JobService,
    private applicationService: ApplicationService,
    private savedJobService: SavedJobService
  ) { }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.fetchJobDetails(+idParam);
      this.checkIfSaved(+idParam);
      this.checkIfApplied(+idParam);
    } else {
      this.error = 'Invalid Job ID';
      this.isLoading = false;
    }
  }

  fetchJobDetails(id: number): void {
    this.isLoading = true;
    this.jobService.getJobById(id).subscribe({
      next: (jobData) => {
        this.job = jobData;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load job', err);
        this.error = 'Failed to load job details. The job may have been removed or is unavailable.';
        this.isLoading = false;
      }
    });
  }

  checkIfSaved(jobId: number): void {
    this.savedJobService.getSavedJobs().subscribe(jobs => {
      this.isSaved = jobs.some(sj => sj.job && sj.job.id === jobId);
    });
  }

  checkIfApplied(jobId: number): void {
    this.applicationService.checkApplicationStatus(jobId).subscribe({
      next: (res) => {
        this.hasApplied = res.hasApplied;
      },
      error: () => {
        this.hasApplied = false;
      }
    });
  }

  toggleSaveJob(): void {
    if (!this.job || this.isSaving) return;

    this.isSaving = true;

    if (this.isSaved) {
      this.displayToast('Job is already saved. Go to Saved Jobs to manage.', 'error');
      this.isSaving = false;
    } else {
      this.savedJobService.saveJob(this.job.id).subscribe({
        next: () => {
          this.isSaved = true;
          this.displayToast('Job saved successfully!');
          this.isSaving = false;
        },
        error: () => {
          this.displayToast('Failed to save job.', 'error');
          this.isSaving = false;
        }
      });
    }
  }

  openApplyModal(): void {
    if (this.hasApplied) return;
    this.showApplyModal = true;
    this.coverLetter = '';
  }

  closeApplyModal(): void {
    this.showApplyModal = false;
  }

  applyForJob(): void {
    if (!this.job || this.hasApplied) return;

    this.isApplying = true;

    this.applicationService.applyForJob(this.job.id, this.coverLetter).subscribe({
      next: () => {
        this.hasApplied = true;
        this.isApplying = false;
        this.closeApplyModal();
        this.displayToast('Application submitted successfully! Good luck!', 'success');
      },
      error: (err) => {
        this.isApplying = false;
        this.displayToast(err.error?.message || 'Failed to submit application. You might have already applied.', 'error');
      }
    });
  }

  displayToast(msg: string, type: 'success' | 'error' = 'success') {
    this.toastMessage = msg;
    this.toastType = type;
    this.showToast = true;
    setTimeout(() => this.showToast = false, 3000);
  }

  parseSkills(skillsText?: string): string[] {
    if (!skillsText) return [];
    return skillsText.split(',').map(s => s.trim()).filter(s => s.length > 0);
  }
}
