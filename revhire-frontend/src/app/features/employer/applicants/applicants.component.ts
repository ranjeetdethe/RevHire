import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApplicationService } from '../../../core/services/application.service';
import { JobService } from '../../../core/services/job.service';
import { ResumeService } from '../../../core/services/resume.service';
import { Application } from '../../../models/application.model';
import { Job } from '../../../models/job.model';
import { ToastComponent } from '../../../shared/components/toast/toast.component';

@Component({
  selector: 'app-employer-applicants',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ToastComponent],
  templateUrl: './applicants.component.html',
  styleUrl: './applicants.component.scss'
})
export class ApplicantsComponent implements OnInit {
  jobId: number | null = null;
  job: Job | null = null;
  applications: Application[] = [];
  filteredApplications: Application[] = [];

  isLoading = true;
  isUpdatingStatus: { [key: number]: boolean } = {};

  // Job Loading
  isJobLoading = true;
  jobError = '';

  // Filters
  searchTerm = '';
  statusFilter = 'ALL';

  // Modals
  showNotesModal = false;
  selectedApp: Application | null = null;
  notesInput = '';

  // Toast
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';
  showToast = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private applicationService: ApplicationService,
    private jobService: JobService,
    private resumeService: ResumeService
  ) { }

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('jobId');
    if (idParam) {
      this.jobId = +idParam;
      this.fetchJobDetails(this.jobId);
      this.fetchApplications(this.jobId);
    } else {
      this.jobError = 'Invalid Job ID';
      this.isJobLoading = false;
      this.isLoading = false;
    }
  }

  fetchJobDetails(id: number) {
    this.isJobLoading = true;
    this.jobService.getJobById(id).subscribe({
      next: (jobData) => {
        this.job = jobData;
        this.isJobLoading = false;
      },
      error: () => {
        this.jobError = 'Failed to load job details. The job may have been removed.';
        this.isJobLoading = false;
      }
    });
  }

  fetchApplications(id: number) {
    this.isLoading = true;
    this.applicationService.getJobApplications(id).subscribe({
      next: (res) => {
        // Enforce descending order by application date
        this.applications = res.content.sort((a, b) => new Date(b.appliedAt || 0).getTime() - new Date(a.appliedAt || 0).getTime());
        this.applyFilters();
        this.isLoading = false;
      },
      error: () => {
        this.applications = [];
        this.filteredApplications = [];
        this.isLoading = false;
        this.displayToast('Failed to load applications for this job.', 'error');
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
    this.filteredApplications = this.applications.filter(app => {
      const name = `${app.applicant?.firstName || ''} ${app.applicant?.lastName || ''}`.toLowerCase();
      const matchSearch = name.includes(this.searchTerm.toLowerCase());
      const matchStatus = this.statusFilter === 'ALL' || app.status === this.statusFilter;

      return (this.searchTerm ? matchSearch : true) && matchStatus;
    });
  }

  countByStatus(status: string): number {
    return this.applications.filter(app => app.status === status).length;
  }

  updateStatus(app: Application, newStatus: string) {
    if (app.status === newStatus || this.isUpdatingStatus[app.id]) return;

    this.isUpdatingStatus[app.id] = true;

    // We can also pass notes inside updateStatus if we wanted, grabbing from employerNotes initially
    this.applicationService.updateStatus(app.id, newStatus, app.employerNotes).subscribe({
      next: () => {
        app.status = newStatus as any; // Cast for TS matching
        this.isUpdatingStatus[app.id] = false;
        this.applyFilters();
        this.displayToast(`Applicant status changed to ${newStatus.replace('_', ' ')}`);
      },
      error: (err) => {
        this.isUpdatingStatus[app.id] = false;
        this.displayToast(err.error?.message || 'Failed to update application status.', 'error');
      }
    });
  }

  openNotesModal(app: Application) {
    this.selectedApp = app;
    this.notesInput = app.employerNotes || '';
    this.showNotesModal = true;
  }

  closeNotesModal() {
    this.showNotesModal = false;
    this.selectedApp = null;
  }

  saveNotes() {
    if (!this.selectedApp) return;

    const app = this.selectedApp;
    this.isUpdatingStatus[app.id] = true;

    // The backend actually allows sending notes through updateStatus or an addNotes endpoint
    // based on ApplicationRestController we use updateStatus natively updating both together.
    this.applicationService.updateStatus(app.id, app.status, this.notesInput).subscribe({
      next: () => {
        app.employerNotes = this.notesInput;
        this.isUpdatingStatus[app.id] = false;
        this.closeNotesModal();
        this.displayToast('Notes updated successfully');
      },
      error: (err) => {
        this.isUpdatingStatus[app.id] = false;
        this.displayToast(err.error?.message || 'Failed to save notes.', 'error');
      }
    });
  }

  downloadResume(app: Application) {
    if (!app.applicant || !app.applicant.userId) {
      this.displayToast('Applicant profile incomplete. Resume not available.', 'error');
      return;
    }
    // Note: Backend handles PDF headers and bytes
    this.resumeService.downloadResume(app.applicant.userId).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        window.open(url); // or force trigger a download anchor
      },
      error: () => {
        this.displayToast('Failed to retrieve resume. Applicant may not have uploaded one.', 'error');
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
