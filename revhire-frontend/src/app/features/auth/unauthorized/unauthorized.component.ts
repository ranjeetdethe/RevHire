import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-unauthorized',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4">
      <div class="text-center">
        <h1 class="text-9xl font-extrabold text-blue-600">403</h1>
        <h2 class="mt-4 text-3xl font-bold text-gray-900 tracking-tight sm:text-5xl">Unauthorized Access</h2>
        <p class="mt-6 text-base text-gray-500 max-w-lg mx-auto">
          We're sorry, it looks like you don't have permission to access this page. Please log in with the appropriate account role to continue.
        </p>
        <div class="mt-10 flex space-x-4 justify-center">
          <a routerLink="/login" class="inline-flex items-center px-6 py-3 border border-transparent text-base font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700">
            Sign In
          </a>
          <button (click)="goBack()" class="inline-flex items-center px-6 py-3 border border-gray-300 text-base font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50">
            Go Back
          </button>
        </div>
      </div>
    </div>
  `
})
export class UnauthorizedComponent {
  goBack() {
    window.history.back();
  }
}
