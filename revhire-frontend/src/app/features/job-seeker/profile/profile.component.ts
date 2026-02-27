import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ProfileService } from '../../../core/services/profile.service';
import { SeekerProfile, Education, WorkExperience, Skill } from '../../../models/profile.model';
import { ToastComponent } from '../../../shared/components/toast/toast.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, ToastComponent],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {
  profile: SeekerProfile | null = null;
  profileCompleteness = 0;
  isLoading = true;
  isSaving = false;

  // Modals state
  activeModal: 'personal' | 'education' | 'experience' | 'skill' | null = null;

  // Forms
  personalForm!: FormGroup;
  eduForm!: FormGroup;
  expForm!: FormGroup;
  skillForm!: FormGroup;

  // Edit states
  editingId: number | null = null;

  // Toast
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';
  showToast = false;

  constructor(
    private profileService: ProfileService,
    private fb: FormBuilder
  ) { }

  ngOnInit() {
    this.initForms();
    this.loadProfile();
  }

  initForms() {
    this.personalForm = this.fb.group({
      location: [''],
      about: ['']
    });

    this.eduForm = this.fb.group({
      institution: ['', Validators.required],
      degree: ['', Validators.required],
      fieldOfStudy: ['', Validators.required],
      startYear: ['', Validators.required],
      endYear: [''],
      isCurrent: [false]
    });

    this.expForm = this.fb.group({
      companyName: ['', Validators.required],
      jobTitle: ['', Validators.required],
      startDate: ['', Validators.required],
      endDate: [''],
      isCurrent: [false],
      description: ['']
    });

    this.skillForm = this.fb.group({
      skillName: ['', Validators.required],
      proficiency: ['INTERMEDIATE', Validators.required]
    });
  }

  loadProfile() {
    this.isLoading = true;
    this.profileService.getProfile().subscribe({
      next: (data) => {
        this.profile = data;
        this.personalForm.patchValue({
          location: data.location || '',
          about: data.about || ''    // NOTE: SeekerProfileRestController returns "about" as resumeText
        });

        // Fetch completeness
        this.profileService.getProfileCompleteness().subscribe(res => {
          this.profileCompleteness = res?.completeness || 0;
        });

        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  displayToast(msg: string, type: 'success' | 'error' = 'success') {
    this.toastMessage = msg;
    this.toastType = type;
    this.showToast = true;
    setTimeout(() => this.showToast = false, 3000);
  }

  closeModal() {
    this.activeModal = null;
    this.editingId = null;
    this.eduForm.reset({ isCurrent: false });
    this.expForm.reset({ isCurrent: false });
    this.skillForm.reset({ proficiency: 'INTERMEDIATE' });
  }

  // --- Personal Info ---
  openPersonalModal() {
    this.activeModal = 'personal';
  }

  savePersonal() {
    this.isSaving = true;
    this.profileService.updateProfile(this.personalForm.value).subscribe({
      next: () => {
        this.loadProfile();
        this.closeModal();
        this.displayToast('Profile updated successfully');
        this.isSaving = false;
      },
      error: () => {
        this.displayToast('Failed to update profile', 'error');
        this.isSaving = false;
      }
    });
  }

  // --- Education ---
  openEduModal(edu?: any) {
    if (edu) {
      this.editingId = edu.id;
      this.eduForm.patchValue(edu);
    }
    this.activeModal = 'education';
  }

  saveEducation() {
    if (this.eduForm.invalid) return;
    this.isSaving = true;

    const req = this.editingId
      ? this.profileService.updateEducation(this.editingId, this.eduForm.value)
      : this.profileService.addEducation(this.eduForm.value);

    req.subscribe({
      next: () => {
        this.loadProfile();
        this.closeModal();
        this.displayToast(this.editingId ? 'Education updated' : 'Education added');
        this.isSaving = false;
      },
      error: () => {
        this.displayToast('Failed to save education', 'error');
        this.isSaving = false;
      }
    });
  }

  deleteEducation(id?: number) {
    if (!id) return;
    if (confirm('Are you sure you want to delete this education entry?')) {
      this.profileService.deleteEducation(id).subscribe(() => {
        this.loadProfile();
        this.displayToast('Education deleted');
      });
    }
  }

  // --- Experience ---
  openExpModal(exp?: any) {
    if (exp) {
      this.editingId = exp.id;
      this.expForm.patchValue(exp);
    }
    this.activeModal = 'experience';
  }

  saveExperience() {
    if (this.expForm.invalid) return;
    this.isSaving = true;

    const req = this.editingId
      ? this.profileService.updateExperience(this.editingId, this.expForm.value)
      : this.profileService.addExperience(this.expForm.value);

    req.subscribe({
      next: () => {
        this.loadProfile();
        this.closeModal();
        this.displayToast(this.editingId ? 'Experience updated' : 'Experience added');
        this.isSaving = false;
      },
      error: () => {
        this.displayToast('Failed to save experience', 'error');
        this.isSaving = false;
      }
    });
  }

  deleteExperience(id?: number) {
    if (!id) return;
    if (confirm('Are you sure you want to delete this experience entry?')) {
      this.profileService.deleteExperience(id).subscribe(() => {
        this.loadProfile();
        this.displayToast('Experience deleted');
      });
    }
  }

  // --- Skills ---
  openSkillModal() {
    this.activeModal = 'skill';
  }

  saveSkill() {
    if (this.skillForm.invalid) return;
    this.isSaving = true;

    this.profileService.addSkill(this.skillForm.value).subscribe({
      next: () => {
        this.loadProfile();
        this.closeModal();
        this.displayToast('Skill added');
        this.isSaving = false;
      },
      error: () => {
        this.displayToast('Failed to add skill', 'error');
        this.isSaving = false;
      }
    });
  }

  deleteSkill(id?: number) {
    if (!id) return;
    this.profileService.deleteSkill(id).subscribe(() => {
      this.loadProfile();
      this.displayToast('Skill removed');
    });
  }
}
