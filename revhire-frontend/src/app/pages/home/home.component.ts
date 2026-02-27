import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {
  jobCategories = [
    { name: 'Software Engineering', icon: 'bi-laptop', count: '1,245 Jobs' },
    { name: 'Data Science', icon: 'bi-bar-chart-steps', count: '850 Jobs' },
    { name: 'Product Management', icon: 'bi-briefcase', count: '430 Jobs' },
    { name: 'Marketing', icon: 'bi-megaphone', count: '620 Jobs' },
    { name: 'Sales', icon: 'bi-currency-dollar', count: '940 Jobs' },
    { name: 'Design', icon: 'bi-palette', count: '510 Jobs' }
  ];

  howItWorks = [
    { title: 'Create an Account', description: 'Sign up for free and complete your professional profile.', icon: 'bi-person-plus' },
    { title: 'Search for Jobs', description: 'Find roles that match your skills, experience, and career goals.', icon: 'bi-search' },
    { title: 'Apply with One Click', description: 'Instantly send your profile and resume to top employers.', icon: 'bi-send-check' },
    { title: 'Get Hired', description: 'Connect with recruiters, interview, and land your dream job.', icon: 'bi-award' }
  ];
}
