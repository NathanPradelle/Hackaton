import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Home } from './home/home';
import { CreateOrder } from './create-order/create-order';
import { PreviousOrders } from './previous-orders/previous-orders';
import { authGuard } from './services/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/home',
    pathMatch: 'full'
  },
  {
    path: 'auth',
    component: Login
  },
  {
    path: 'home',
    component: Home,
    canActivate: [authGuard]
  },
  {
    path: 'orders/new',
    component: CreateOrder,
    canActivate: [authGuard]
  },
  {
    path: 'orders/previous',
    component: PreviousOrders,
    canActivate: [authGuard]
  },
];
