import { Component, Input, OnChanges } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-status-badge',
  standalone: true,
  imports: [CommonModule],
  template: `
    <span class="badge rounded-pill fw-semibold px-3 py-1 bg-opacity-10 w-fit-content shadow-sm" [ngClass]="badgeClass">
      <i class="bi me-1" [ngClass]="iconClass"></i> {{ displayStatus }}
    </span>
  `,
  styles: [`
    .w-fit-content { width: fit-content; }
    .bg-opacity-10 { border: 1px solid currentColor; }
  `]
})
export class StatusBadgeComponent implements OnChanges {
  @Input() status: string = '';

  badgeClass = '';
  iconClass = '';
  displayStatus = '';

  ngOnChanges() {
    this.updateBadge();
  }

  private updateBadge() {
    const s = (this.status || '').toUpperCase();
    this.displayStatus = this.status;

    switch (s) {
      case 'PENDING':
      case 'APPLIED':
        this.badgeClass = 'bg-warning text-warning border-warning';
        this.iconClass = 'bi-hourglass-split';
        break;
      case 'UNDER_REVIEW':
        this.badgeClass = 'bg-info text-info border-info';
        this.iconClass = 'bi-eye';
        break;
      case 'INTERVIEWING':
        this.badgeClass = 'bg-primary text-primary border-primary';
        this.iconClass = 'bi-people';
        break;
      case 'ACCEPTED':
      case 'HIRED':
      case 'OFFERED':
        this.badgeClass = 'bg-success text-success border-success';
        this.iconClass = 'bi-check-circle';
        break;
      case 'REJECTED':
      case 'WITHDRAWN':
        this.badgeClass = 'bg-danger text-danger border-danger';
        this.iconClass = 'bi-x-circle';
        break;
      case 'OPEN':
        this.badgeClass = 'bg-success text-success border-success';
        this.iconClass = 'bi-unlock';
        break;
      case 'CLOSED':
        this.badgeClass = 'bg-secondary text-secondary border-secondary';
        this.iconClass = 'bi-lock';
        break;
      default:
        this.badgeClass = 'bg-secondary text-secondary border-secondary';
        this.iconClass = 'bi-info-circle';
        this.displayStatus = s || 'UNKNOWN';
    }
  }
}
