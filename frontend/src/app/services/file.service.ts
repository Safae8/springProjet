import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { environment } from '../../environments/environment';
import { File, AccessRequest } from '../models/file.model';

@Injectable({
  providedIn: 'root'
})
export class FileService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  // File operations
  uploadFile(file: File, description: string, isPublic: boolean): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('description', description);
    formData.append('isPublic', isPublic.toString());
    
    return this.http.post(`${this.apiUrl}/files/upload`, formData)
      .pipe(catchError(this.handleError));
  }

  getMyFiles(): Observable<File[]> {
    return this.http.get<File[]>(`${this.apiUrl}/files/my-files`)
      .pipe(catchError(this.handleError));
  }

  getPublicFiles(): Observable<File[]> {
    return this.http.get<File[]>(`${this.apiUrl}/files/public`)
      .pipe(catchError(this.handleError));
  }

  downloadFile(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/files/download/${id}`, {
      responseType: 'blob'
    }).pipe(catchError(this.handleError));
  }

  deleteFile(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/files/${id}`)
      .pipe(catchError(this.handleError));
  }

  // Access request operations
  requestAccess(fileId: number, message: string): Observable<AccessRequest> {
    return this.http.post<AccessRequest>(`${this.apiUrl}/requests/request-access`, {
      fileId,
      message
    }).pipe(catchError(this.handleError));
  }

  getMyRequests(): Observable<AccessRequest[]> {
    return this.http.get<AccessRequest[]>(`${this.apiUrl}/requests/my-requests`)
      .pipe(catchError(this.handleError));
  }

  getReceivedRequests(): Observable<AccessRequest[]> {
    return this.http.get<AccessRequest[]>(`${this.apiUrl}/requests/received-requests`)
      .pipe(catchError(this.handleError));
  }

  updateRequestStatus(requestId: number, status: string): Observable<AccessRequest> {
    return this.http.put<AccessRequest>(`${this.apiUrl}/requests/${requestId}/status`, {
      status
    }).pipe(catchError(this.handleError));
  }

  deleteRequest(requestId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/requests/${requestId}`)
      .pipe(catchError(this.handleError));
  }

  private handleError(error: any): Observable<never> {
    console.error('An error occurred:', error);
    return throwError(() => error);
  }
}