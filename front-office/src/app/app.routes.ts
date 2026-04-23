import { Routes } from '@angular/router';
import { Login } from './login/login';
import { authGuard } from './services/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/auth',
    pathMatch: 'full'
  },
  {
    path: 'auth',
    component: Login
  },
];
