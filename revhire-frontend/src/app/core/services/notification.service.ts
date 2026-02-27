import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Notification } from '../../models/notification.model';
import { BehaviorSubject, Observable, Subscription, timer } from 'rxjs';
import { switchMap, tap, map } from 'rxjs/operators';
import { AuthService } from './auth.service';

@Injectable({
    providedIn: 'root'
})
export class NotificationService {
    private apiUrl = `${environment.apiUrl}/notifications`;
    public unreadCount$ = new BehaviorSubject<number>(0);
    private pollingSubscription?: Subscription;

    constructor(private http: HttpClient, private authService: AuthService) { }

    getAll(): Observable<Notification[]> {
        return this.http.get<Notification[]>(`${this.apiUrl}/my`);
    }

    markRead(id: number): Observable<any> {
        return this.http.put(`${this.apiUrl}/${id}/read`, {}).pipe(
            tap(() => this.updateUnreadCount())
        );
    }

    markAllRead(): Observable<any> {
        return this.http.put(`${this.apiUrl}/read-all`, {}).pipe(
            tap(() => this.unreadCount$.next(0))
        );
    }

    deleteNotification(id: number): Observable<any> {
        return this.http.delete(`${this.apiUrl}/${id}`).pipe(
            tap(() => this.updateUnreadCount())
        );
    }

    startPolling() {
        if (this.pollingSubscription) return;
        this.pollingSubscription = timer(0, environment.notificationPollInterval || 30000).pipe(
            switchMap(() => {
                if (this.authService.isLoggedIn()) {
                    return this.http.get<{ count: number }>(`${this.apiUrl}/unread-count`).pipe(
                        map(res => res.count)
                    );
                }
                return new Observable<number>(observer => observer.next(0));
            })
        ).subscribe(count => {
            this.unreadCount$.next(count || 0);
        });
    }

    stopPolling() {
        if (this.pollingSubscription) {
            this.pollingSubscription.unsubscribe();
            this.pollingSubscription = undefined;
        }
    }

    private updateUnreadCount() {
        if (this.authService.isLoggedIn()) {
            this.http.get<{ count: number }>(`${this.apiUrl}/unread-count`).subscribe(res => {
                this.unreadCount$.next(res.count || 0);
            });
        }
    }
}
