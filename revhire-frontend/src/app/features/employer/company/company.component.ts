import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { EmployerService } from '../../../core/services/employer.service';
import { Employer } from '../../../models/employer.model';
import { ToastComponent } from '../../../shared/components/toast/toast.component';

@Component({
  selector: 'app-employer-company',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, ToastComponent],
  templateUrl: './company.component.html',
  styleUrl: './company.component.scss'
})
export class CompanyComponent implements OnInit {
  companyForm!: FormGroup;
  isLoading = true;
  isSaving = false;
  employerData: Employer | null = null;

  // Toast
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';
  showToast = false;

  constructor(
    private fb: FormBuilder,
    private employerService: EmployerService
  ) { }

  ngOnInit() {
    this.companyForm = this.fb.group({
      companyName: ['', Validators.required],
      industry: ['', Validators.required],
      location: ['', Validators.required],
      companySize: [''],
      website: [''],
      description: ['', [Validators.required, Validators.minLength(50)]]
    });

    this.loadCompanyProfile();
  }

  loadCompanyProfile() {
    this.isLoading = true;
    this.employerService.getCompanyProfile().subscribe({
      next: (data) => {
        this.employerData = data;
        this.companyForm.patchValue({
          companyName: data.companyName,
          industry: data.industry,
          location: data.location,
          companySize: data.companySize,
          website: data.website,
          description: data.description
        });
        this.isLoading = false;
      },
      error: () => {
        this.displayToast('Failed to load company profile', 'error');
        this.isLoading = false;
      }
    });
  }

  onSubmit() {
    if (this.companyForm.invalid) {
      this.markFormGroupTouched(this.companyForm);
      this.displayToast('Please complete all required fields correctly', 'error');
      return;
    }

    this.isSaving = true;
    this.employerService.updateCompanyProfile(this.companyForm.value).subscribe({
      next: (res) => {
        this.employerData = res;
        this.isSaving = false;
        this.displayToast('Company profile updated successfully', 'success');
        this.companyForm.markAsPristine();
      },
      error: (err) => {
        this.isSaving = false;
        this.displayToast(err.error?.message || 'Failed to update company profile', 'error');
      }
    });
  }

  get f() {
    return this.companyForm.controls;
  }

  private markFormGroupTouched(formGroup: FormGroup) {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }

  displayToast(msg: string, type: 'success' | 'error' = 'success') {
    this.toastMessage = msg;
    this.toastType = type;
    this.showToast = true;
    setTimeout(() => this.showToast = false, 3000);
  }
}
