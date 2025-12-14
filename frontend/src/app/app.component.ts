import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './services/auth.service';
import { FileService } from './services/file.service';

@Component({
  selector: 'app-root',
  template: `
    <mat-toolbar color="primary">
      <button mat-icon-button (click)="toggleSidenav()" *ngIf="isLoggedIn">
        <mat-icon>menu</mat-icon>
      </button>
      
      <span class="logo" routerLink="/dashboard" *ngIf="isLoggedIn">DriveApp</span>
      <span class="logo" routerLink="/login" *ngIf="!isLoggedIn">DriveApp</span>
      
      <span class="spacer"></span>
      
      <ng-container *ngIf="isLoggedIn && currentUser">
        <mat-icon class="user-icon">person</mat-icon>
        <span class="user-name">{{currentUser.firstName}} {{currentUser.lastName}}</span>
        
        <button mat-icon-button [matMenuTriggerFor]="menu">
          <mat-icon>more_vert</mat-icon>
        </button>
        
        <mat-menu #menu="matMenu">
          <button mat-menu-item (click)="logout()">
            <mat-icon>logout</mat-icon>
            <span>Logout</span>
          </button>
        </mat-menu>
      </ng-container>
    </mat-toolbar>

    <mat-drawer-container class="sidenav-container" *ngIf="isLoggedIn">
      <mat-drawer mode="side" [opened]="sidenavOpened">
        <mat-nav-list>
          <a mat-list-item routerLink="/dashboard" routerLinkActive="active">
            <mat-icon>dashboard</mat-icon>
            <span>Dashboard</span>
          </a>
          
          <a mat-list-item (click)="openUploadDialog()">
            <mat-icon>cloud_upload</mat-icon>
            <span>Upload File</span>
          </a>
          
          <a mat-list-item routerLink="/dashboard" [queryParams]="{tab: 'my-files'}" routerLinkActive="active">
            <mat-icon>folder</mat-icon>
            <span>My Files</span>
          </a>
          
          <a mat-list-item routerLink="/dashboard" [queryParams]="{tab: 'public-files'}" routerLinkActive="active">
            <mat-icon>public</mat-icon>
            <span>Public Files</span>
          </a>
          
          <a mat-list-item routerLink="/dashboard" [queryParams]="{tab: 'requests'}" routerLinkActive="active">
            <mat-icon>notifications</mat-icon>
            <span>Requests</span>
            <mat-chip class="notification-chip" *ngIf="pendingRequestsCount > 0">
              {{pendingRequestsCount}}
            </mat-chip>
          </a>
        </mat-nav-list>
      </mat-drawer>
      
      <mat-drawer-content>
        <div class="content">
          <router-outlet></router-outlet>
        </div>
      </mat-drawer-content>
    </mat-drawer-container>
    
    <div *ngIf="!isLoggedIn">
      <router-outlet></router-outlet>
    </div>
  `,
  styles: [`
    .logo {
      margin-left: 10px;
      font-size: 20px;
      font-weight: bold;
      cursor: pointer;
    }
    
    .spacer {
      flex: 1 1 auto;
    }
    
    .user-icon {
      margin-right: 8px;
    }
    
    .user-name {
      margin-right: 16px;
    }
    
    .sidenav-container {
      height: calc(100vh - 64px);
    }
    
    .content {
      padding: 20px;
    }
    
    mat-nav-list a {
      display: flex;
      align-items: center;
    }
    
    mat-nav-list mat-icon {
      margin-right: 12px;
    }
    
    .active {
      background-color: rgba(63, 81, 181, 0.1);
      color: #3f51b5;
    }
    
    .notification-chip {
      margin-left: auto !important;
      background-color: #f44336 !important;
      color: white !important;
      font-size: 12px !important;
      height: 20px !important;
      min-width: 20px !important;
    }
  `]
})
export class AppComponent implements OnInit {
  isLoggedIn = false;
  currentUser: any = null;
  sidenavOpened = true;
  pendingRequestsCount = 0;

  constructor(
    private authService: AuthService,
    private fileService: FileService,
    private router: Router
  ) {}

  ngOnInit() {
    this.authService.currentUser.subscribe(user => {
      this.isLoggedIn = !!user;
      this.currentUser = user;
      
      if (this.isLoggedIn) {
        this.loadPendingRequests();
      }
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  toggleSidenav() {
    this.sidenavOpened = !this.sidenavOpened;
  }

  openUploadDialog() {
    // Cette fonction sera implémentée plus tard
    alert('Upload dialog will open here');
  }

  loadPendingRequests() {
    this.fileService.getReceivedRequests().subscribe(requests => {
      this.pendingRequestsCount = requests.filter(r => r.status === 'PENDING').length;
    });
  }
}