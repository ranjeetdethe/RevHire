import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { JobService } from '../../../core/services/job.service';
import { ToastComponent } from '../../../shared/components/toast/toast.component';

@Component({
  selector: 'app-job-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, ToastComponent],
  templateUrl: './job-form.component.html',
  styleUrl: './job-form.component.scss'
})
export class JobFormComponent implements OnInit {
  jobForm!: FormGroup;
  isEditMode = false;
  jobId: number | null = null;
  isLoading = false;
  isSaving = false;
  errorMessage = '';

  // Toast
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';
  showToast = false;

  constructor(
    private fb: FormBuilder,
    private jobService: JobService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');

    if (idParam && idParam !== 'new') {
      this.jobId = +idParam;
      this.isEditMode = true;
    }

    this.jobForm = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(100)]],
      jobType: ['FULL_TIME', Validators.required],
      location: ['', Validators.required],
      status: ['OPEN', Validators.required],

      experienceYearsMin: [0, [Validators.required, Validators.min(0)]],
      openingsCount: [1, [Validators.required, Validators.min(1)]],

      salaryMin: [null],
      salaryMax: [null],

      educationRequired: ['', Validators.required],
      requiredSkills: ['', Validators.required], // Comma separated expectation layout

      description: ['', [Validators.required, Validators.minLength(50)]],
      deadline: ['']
    });

    if (this.isEditMode) {
      this.loadJobData();
    }
  }

  loadJobData() {
    this.isLoading = true;
    if (this.jobId) {
      this.jobService.getJobById(this.jobId).subscribe({
        next: (job) => {
          // Format date if deadline exists to fit input[type="date"]
          let formattedDeadline = '';
          if (job.deadline) {
            const d = new Date(job.deadline);
            formattedDeadline = d.toISOString().split('T')[0];
          }

          this.jobForm.patchValue({
            title: job.title,
            jobType: job.jobType,
            location: job.location,
            status: job.status,
            experienceYearsMin: job.experienceYearsMin,
            openingsCount: job.openingsCount,
            salaryMin: job.salaryMin,
            salaryMax: job.salaryMax,
            educationRequired: job.educationRequired,
            requiredSkills: job.requiredSkills,
            description: job.description,
            deadline: formattedDeadline
          });
          this.isLoading = false;
        },
        error: () => {
          this.errorMessage = 'Failed to load job details. The job may no longer exist.';
          this.isLoading = false;
          this.displayToast(this.errorMessage, 'error');
        }
      });
    }
  }

  onSubmit() {
    if (this.jobForm.invalid) {
      this.markFormGroupTouched(this.jobForm);
      this.displayToast('Please fix the errors in the form before submitting.', 'error');
      return;
    }

    this.isSaving = true;
    this.errorMessage = '';

    const obs$ = this.isEditMode
      ? this.jobService.updateJob(this.jobId!, this.jobForm.value)
      : this.jobService.createJob(this.jobForm.value);

    obs$.subscribe({
      next: () => {
        this.isSaving = false;
        this.displayToast(`Job ${this.isEditMode ? 'updated' : 'published'} successfully!`, 'success');
        setTimeout(() => {
          this.router.navigate(['/employer/jobs']);
        }, 1500);
      },
      error: (err) => {
        this.isSaving = false;
        this.errorMessage = err.error?.message || 'An error occurred while saving the job.';
        this.displayToast(this.errorMessage, 'error');

        // Scroll to top to see error summary if added
        window.scrollTo({ top: 0, behavior: 'smooth' });
      }
    });
  }

  // Helper to mark all controls as touched to trigger validation styles natively
  private markFormGroupTouched(formGroup: FormGroup) {
    Object.keys(formGroup.controls).forEach(field => {
      const control = formGroup.get(field);
      control?.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  // Getters for form validation states
  get f() { return this.jobForm.controls; }

  displayToast(msg: string, type: 'success' | 'error' = 'success') {
    this.toastMessage = msg;
    this.toastType = type;
    this.showToast = true;
    setTimeout(() => this.showToast = false, 3000);
  }
}
