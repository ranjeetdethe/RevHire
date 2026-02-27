import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NotificationService } from '../../../core/services/notification.service';
import { Notification } from '../../../models/notification.model';
import { ToastComponent } from '../../../shared/components/toast/toast.component';

@Component({
  selector: 'app-employer-notifications',
  standalone: true,
  imports: [CommonModule, RouterModule, ToastComponent],
  templateUrl: './notifications.component.html',
  styleUrl: './notifications.component.scss'
})
export class NotificationsComponent implements OnInit, OnDestroy {
  notifications: Notification[] = [];
  isLoading = true;
  isProcessing: { [key: number]: boolean } = {};
  isClearingAll = false;

  // Toast
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';
  showToast = false;

  constructor(private notificationService: NotificationService) { }

  ngOnInit() {
    this.loadNotifications();
  }

  ngOnDestroy() {
    // Service handles polling, we just read from the UI state here, but if we opened websockets we would destroy here
  }

  loadNotifications() {
    this.isLoading = true;
    this.notificationService.getAll().subscribe({
      next: (data) => {
        // Sort newest first
        this.notifications = data.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
        this.isLoading = false;

        // Trigger a background read count update
        this.notificationService.unreadCount$.subscribe(); // just relying on service state sync
      },
      error: () => {
        this.notifications = [];
        this.isLoading = false;
        this.displayToast('Failed to load notifications', 'error');
      }
    });
  }

  markAsRead(id: number) {
    if (this.isProcessing[id]) return;
    this.isProcessing[id] = true;

    this.notificationService.markRead(id).subscribe({
      next: () => {
        const notif = this.notifications.find(n => n.id === id);
        if (notif) notif.isRead = true;
        this.isProcessing[id] = false;
      },
      error: () => {
        this.isProcessing[id] = false;
        this.displayToast('Failed to mark read', 'error');
      }
    });
  }

  markAllAsRead() {
    if (this.isClearingAll || this.unreadCount === 0) return;
    this.isClearingAll = true;

    this.notificationService.markAllRead().subscribe({
      next: () => {
        this.notifications.forEach(n => n.isRead = true);
        this.isClearingAll = false;
        this.displayToast('All notifications marked as read');
      },
      error: () => {
        this.isClearingAll = false;
        this.displayToast('Failed to mark all as read', 'error');
      }
    });
  }

  deleteNotification(id: number, event: Event) {
    event.stopPropagation(); // prevent triggering mark-as-read click event
    if (this.isProcessing[id]) return;
    this.isProcessing[id] = true;

    this.notificationService.deleteNotification(id).subscribe({
      next: () => {
        this.notifications = this.notifications.filter(n => n.id !== id);
        this.isProcessing[id] = false;
        this.displayToast('Notification deleted');
      },
      error: () => {
        this.isProcessing[id] = false;
        this.displayToast('Failed to delete notification', 'error');
      }
    });
  }

  handleNotificationClick(notification: Notification) {
    if (!notification.isRead) {
      this.markAsRead(notification.id);
    }
    // If we wanted to route dynamically based on referenceType:
    // if (notification.referenceType === 'APPLICATION') route to /employer/jobs/...
  }

  get unreadCount() {
    return this.notifications.filter(n => !n.isRead).length;
  }

  displayToast(msg: string, type: 'success' | 'error' = 'success') {
    this.toastMessage = msg;
    this.toastType = type;
    this.showToast = true;
    setTimeout(() => this.showToast = false, 3000);
  }
}
