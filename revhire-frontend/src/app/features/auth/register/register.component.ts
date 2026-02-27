import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent implements OnInit {
  step = 1;
  selectedRole = '';
  registerForm!: FormGroup;
  isLoading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit() {
    this.registerForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      phone: ['', Validators.required],
      location: [''],
      employmentStatus: [''],
      companyName: [''],
      companyIndustry: [''],
      companySize: [''],
      companyLocation: [''],
      securityQuestion: ['What is your pet name?', Validators.required],
      securityAnswer: ['', Validators.required]
    });
  }

  selectRole(role: string) {
    this.selectedRole = role;
  }

  nextStep() {
    if (this.selectedRole) {
      this.step = 2;

      // Setup dynamic validation based on role
      if (this.selectedRole === 'EMPLOYER') {
        this.registerForm.get('companyName')?.setValidators([Validators.required]);
        this.registerForm.get('companyIndustry')?.setValidators([Validators.required]);
      } else {
        this.registerForm.get('companyName')?.clearValidators();
        this.registerForm.get('companyIndustry')?.clearValidators();
      }
      this.registerForm.get('companyName')?.updateValueAndValidity();
      this.registerForm.get('companyIndustry')?.updateValueAndValidity();
    }
  }

  onSubmit() {
    if (this.registerForm.invalid) return;

    this.isLoading = true;
    this.errorMessage = '';

    const req = { ...this.registerForm.value, role: this.selectedRole };

    this.authService.register(req).subscribe({
      next: () => {
        // Auto-login upon successful registration
        this.authService.login(req.email, req.password).subscribe({
          next: () => {
            if (this.selectedRole === 'JOB_SEEKER') {
              this.router.navigate(['/job-seeker/dashboard']);
            } else if (this.selectedRole === 'EMPLOYER') {
              this.router.navigate(['/employer/dashboard']);
            } else {
              this.router.navigate(['/']); // fallback
            }
          },
          error: () => {
            this.router.navigate(['/auth/login']);
          }
        });
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.message || 'Registration failed. Please try again.';
      }
    });
  }
}
