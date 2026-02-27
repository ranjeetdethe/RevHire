import {
    Component,
    OnInit,
    OnDestroy,
    HostListener,
    ElementRef
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { User } from '../../models/user.model';
import { NotificationService } from '../../core/services/notification.service';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-dashboard-layout',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './dashboard-layout.component.html',
    styleUrls: ['./dashboard-layout.component.scss']
})
export class DashboardLayoutComponent implements OnInit, OnDestroy {
    user: User | null = null;
    unreadCount = 0;
    isDropdownOpen = false;

    private notifSub?: Subscription;

    constructor(
        private authService: AuthService,
        private notificationService: NotificationService,
        private router: Router,
        private eRef: ElementRef
    ) { }

    ngOnInit(): void {
        this.user = this.authService.getCurrentUser();

        if (this.authService.isLoggedIn()) {
            this.notificationService.startPolling();
        }

        this.notifSub = this.notificationService.unreadCount$.subscribe(
            count => (this.unreadCount = count)
        );
    }

    ngOnDestroy(): void {
        this.notifSub?.unsubscribe();
        this.notificationService.stopPolling();
    }

    toggleDropdown(event: Event): void {
        event.stopPropagation();
        this.isDropdownOpen = !this.isDropdownOpen;
    }

    @HostListener('document:click', ['$event'])
    closeOnOutsideClick(event: Event): void {
        const clickedInside = this.eRef.nativeElement.contains(event.target);
        if (!clickedInside) {
            this.isDropdownOpen = false;
        }
    }

    navigateToProfile(): void {
        this.isDropdownOpen = false;
        this.router.navigate(['/job-seeker/profile']);
    }

    onLogout(): void {
        this.isDropdownOpen = false;
        this.notificationService.stopPolling();
        this.authService.logout();
    }
}