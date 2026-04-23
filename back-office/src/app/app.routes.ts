import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { CamionsComponent } from './camions/camions.component';
import { UsersComponent } from './users/users.component';
import { CommandesComponent } from './commandes/commandes.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { authGuard } from './auth/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'camions', component: CamionsComponent, canActivate: [authGuard] },
  { path: 'users', component: UsersComponent, canActivate: [authGuard] },
  { path: 'commandes', component: CommandesComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: 'login' }
];
