export interface Notification {
    id: number;
    title: string;
    message: string;
    type: string;
    isRead: boolean;
    referenceId?: number;
    referenceType?: string;
    createdAt: string;
}
