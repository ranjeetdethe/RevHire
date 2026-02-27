import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ApplicationService } from '../../../core/services/application.service';
import { Application } from '../../../models/application.model';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-applications',
  standalone: true,
  imports: [CommonModule, RouterModule, StatusBadgeComponent],
  templateUrl: './applications.component.html',
  styleUrl: './applications.component.scss'
})
export class ApplicationsComponent implements OnInit {
  applications: Application[] = [];
  filteredApplications: Application[] = [];
  isLoading = true;

  // Filters
  searchTerm = '';
  statusFilter = 'ALL';

  constructor(private appService: ApplicationService) { }

  ngOnInit() {
    this.loadApplications();
  }

  loadApplications() {
    this.isLoading = true;
    this.appService.getMyApplications().subscribe({
      next: (apps) => {
        // Enforce descending order by default
        this.applications = apps.sort((a, b) => new Date(b.appliedAt || 0).getTime() - new Date(a.appliedAt || 0).getTime());
        this.applyFilters();
        this.isLoading = false;
      },
      error: () => {
        this.applications = [];
        this.filteredApplications = [];
        this.isLoading = false;
      }
    });
  }

  onSearchChange(event: any) {
    this.searchTerm = event.target.value;
    this.applyFilters();
  }

  onFilterChange(status: string) {
    this.statusFilter = status;
    this.applyFilters();
  }

  applyFilters() {
    this.filteredApplications = this.applications.filter(app => {
      // @ts-ignore. Mapping to job dto from backend json conversion logic handling missing typings
      const jobTitleMatch = app.job?.title?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        // @ts-ignore
        app.job?.companyName?.toLowerCase().includes(this.searchTerm.toLowerCase());

      const statusMatch = this.statusFilter === 'ALL' || app.status === this.statusFilter;

      return (this.searchTerm ? jobTitleMatch : true) && statusMatch;
    });
  }
}
