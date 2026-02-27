import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class LoadingService {
    private _isLoading = new BehaviorSubject<boolean>(false);
    public isLoading$ = this._isLoading.asObservable();
    private requestCount = 0;

    show() {
        if (this.requestCount === 0) {
            this._isLoading.next(true);
        }
        this.requestCount++;
    }

    hide() {
        this.requestCount--;
        if (this.requestCount <= 0) {
            this.requestCount = 0;
            this._isLoading.next(false);
        }
    }
}
