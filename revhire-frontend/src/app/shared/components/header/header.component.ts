import { Component, OnInit, OnDestroy, HostListener, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { Subscription } from 'rxjs';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent implements OnInit, OnDestroy {
  isLoggedIn = false;
  role: string | null = null;
  user: any = null;
  unreadCount = 0;
  isDropdownOpen = false;

  private authSub: Subscription | undefined;
  private notifSub: Subscription | undefined;

  constructor(
    public authService: AuthService,
    private notificationService: NotificationService,
    private router: Router,
    private eRef: ElementRef
  ) { }

  ngOnInit() {
    this.authSub = this.authService.isAuthenticated$.subscribe(isAuth => {
      this.isLoggedIn = isAuth;
      if (this.isLoggedIn) {
        this.notificationService.startPolling();
      } else {
        this.notificationService.stopPolling();
      }
    });

    // Also subscribe to user updates
    this.authService.currentUser$.subscribe(u => {
      this.user = u;
      this.role = this.authService.getRole();
    });

    this.notifSub = this.notificationService.unreadCount$.subscribe(count => {
      this.unreadCount = count;
    });
  }

  ngOnDestroy() {
    if (this.authSub) this.authSub.unsubscribe();
    if (this.notifSub) this.notifSub.unsubscribe();
    this.notificationService.stopPolling();
  }

  toggleDropdown(event: Event) {
    event.preventDefault();
    event.stopPropagation();
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  @HostListener('document:click', ['$event'])
  closeOnOutsideClick(event: Event) {
    if (!this.eRef.nativeElement.querySelector('.dropdown')?.contains(event.target)) {
      this.isDropdownOpen = false;
    }
  }

  navigateToProfile() {
    this.isDropdownOpen = false;
    this.router.navigate(['/job-seeker/profile']);
  }

  onLogout() {
    this.isDropdownOpen = false;
    this.notificationService.stopPolling();
    this.authService.logout();
  }
}
