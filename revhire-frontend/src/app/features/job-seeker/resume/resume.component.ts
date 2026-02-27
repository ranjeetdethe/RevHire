import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ResumeService } from '../../../core/services/resume.service';
import { AuthService } from '../../../core/services/auth.service';
import { ToastComponent } from '../../../shared/components/toast/toast.component';

@Component({
  selector: 'app-resume',
  standalone: true,
  imports: [CommonModule, RouterModule, ToastComponent],
  templateUrl: './resume.component.html',
  styleUrl: './resume.component.scss'
})
export class ResumeComponent implements OnInit {
  resumeMetadata: any = null;
  isLoading = true;
  isUploading = false;
  selectedFile: File | null = null;
  dragOver = false;

  toastMessage = '';
  toastType: 'success' | 'error' = 'success';
  showToast = false;

  constructor(
    private resumeService: ResumeService,
    private authService: AuthService
  ) { }

  ngOnInit() {
    this.loadResume();
  }

  loadResume() {
    this.isLoading = true;
    this.resumeService.getMyResume().subscribe({
      next: (data) => {
        this.resumeMetadata = data;
        this.isLoading = false;
      },
      error: (err) => {
        // 404 is expected if they don't have a resume yet
        if (err.status === 404) {
          this.resumeMetadata = null;
        } else {
          this.displayToast('Failed to load resume details', 'error');
        }
        this.isLoading = false;
      }
    });
  }

  displayToast(msg: string, type: 'success' | 'error' = 'success') {
    this.toastMessage = msg;
    this.toastType = type;
    this.showToast = true;
    setTimeout(() => this.showToast = false, 3000);
  }

  // --- Drag and Drop Handlers ---
  onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.dragOver = true;
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.dragOver = false;
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.dragOver = false;
    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      this.handleFileSelect(files[0]);
    }
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.handleFileSelect(file);
    }
  }

  handleFileSelect(file: File) {
    if (file.type !== 'application/pdf') {
      this.displayToast('Please select a PDF file', 'error');
      return;
    }

    // Max 5MB
    if (file.size > 5 * 1024 * 1024) {
      this.displayToast('File size must be under 5MB', 'error');
      return;
    }

    this.selectedFile = file;
  }

  removeSelectedFile() {
    this.selectedFile = null;
  }

  uploadResume() {
    if (!this.selectedFile) return;

    this.isUploading = true;
    this.resumeService.uploadResume(this.selectedFile).subscribe({
      next: () => {
        this.displayToast('Resume uploaded successfully');
        this.selectedFile = null;
        this.isUploading = false;
        this.loadResume(); // refresh metadata
      },
      error: (err) => {
        this.displayToast(err.error?.message || 'Failed to upload resume', 'error');
        this.isUploading = false;
      }
    });
  }

  downloadResume() {
    const user = this.authService.getCurrentUser();
    if (!user || !user.id) return;

    this.resumeService.downloadResume(user.id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = this.resumeMetadata?.fileName || 'resume.pdf';
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
      },
      error: () => {
        this.displayToast('Failed to download resume', 'error');
      }
    });
  }

  deleteResume() {
    if (confirm('Are you sure you want to delete your resume? This cannot be undone.')) {
      this.resumeService.deleteResume().subscribe({
        next: () => {
          this.displayToast('Resume deleted successfully');
          this.resumeMetadata = null;
        },
        error: () => {
          this.displayToast('Failed to delete resume', 'error');
        }
      });
    }
  }
}
