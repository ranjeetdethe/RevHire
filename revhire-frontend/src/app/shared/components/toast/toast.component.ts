import { Component, Input, OnChanges } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="toast-container position-fixed bottom-0 end-0 p-3" style="z-index: 1055;">
        <div class="toast align-items-center text-white border-0 animate__animated animate__fadeInUp animate__faster shadow-lg" 
             [ngClass]="type === 'success' ? 'bg-success' : 'bg-danger'"
             [class.show]="show" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body fw-medium d-flex align-items-center gap-2">
                    <i class="bi" [ngClass]="type === 'success' ? 'bi-check-circle-fill' : 'bi-exclamation-triangle-fill'"></i>
                    {{ message }}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" (click)="show = false" aria-label="Close"></button>
            </div>
        </div>
    </div>
  `,
  styles: [`
    .toast-container { transition: all 0.3s ease; }
  `]
})
export class ToastComponent implements OnChanges {
  @Input() message: string = '';
  @Input() type: 'success' | 'error' = 'success';
  @Input() show: boolean = false;

  ngOnChanges() { }
}
