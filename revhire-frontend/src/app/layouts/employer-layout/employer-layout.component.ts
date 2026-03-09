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
    selector: 'app-employer-layout',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './employer-layout.component.html',
    styleUrls: ['./employer-layout.component.scss']
})
export class EmployerLayoutComponent implements OnInit, OnDestroy {
    user: User | null = null;
    unreadCount = 0;
    isDropdownOpen = false;
    isSidebarCollapsed = false;

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

        this.checkScreenSize();
    }

    ngOnDestroy(): void {
        this.notifSub?.unsubscribe();
        this.notificationService.stopPolling();
    }

    @HostListener('window:resize')
    onResize() {
        this.checkScreenSize();
    }

    checkScreenSize() {
        if (window.innerWidth < 992) {
            this.isSidebarCollapsed = true;
        } else {
            this.isSidebarCollapsed = false;
        }
    }

    toggleSidebar() {
        this.isSidebarCollapsed = !this.isSidebarCollapsed;
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
        this.router.navigate(['/employer/company']);
    }

    onLogout(): void {
        this.isDropdownOpen = false;
        this.notificationService.stopPolling();
        this.authService.logout();
    }
}
