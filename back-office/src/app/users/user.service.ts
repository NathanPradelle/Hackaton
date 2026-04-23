import { Injectable, signal, inject } from '@angular/core';
import { User } from '../models/user.model';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class UserService {
  // Mock data aligned with the 'User' interface and backend entity
  private usersSignal = signal<User[]>([
    { id: 1, username: 'admin_principal', role: 'ADMIN' },
    { id: 2, username: 'jean_chauffeur', role: 'DRIVER' }, // Role updated to match backend 'Driver' entity
    { id: 3, username: 'entreprise_dupont', role: 'CLIENT' },
  ]);

  readonly users = this.usersSignal.asReadonly();

  // private http = inject(HttpClient);
  // private apiUrl = `${environment.apiUrl}/users`; // API route is conventional

  /* loadUsers() { ... } */

  addUser(user: User) {
    // this.http.post<User>(this.apiUrl, user).subscribe(newUser => {
    //   this.usersSignal.update(users => [...users, newUser]);
    // });
    this.usersSignal.update(users => [...users, { ...user, id: Math.floor(Math.random() * 1000) }]); // Mock ID generation
  }

  updateUser(modifiedUser: User) {
    // this.http.put<User>(`${this.apiUrl}/${modifiedUser.id}`, modifiedUser).subscribe(updatedUser => {
    //   this.usersSignal.update(users => users.map(u => u.id === updatedUser.id ? updatedUser : u));
    // });
    this.usersSignal.update(users => users.map(u => u.id === modifiedUser.id ? modifiedUser : u));
  }

  deleteUser(id: number) {
    // this.http.delete(`${this.apiUrl}/${id}`).subscribe(() => {
    //   this.usersSignal.update(users => users.filter(u => u.id !== id));
    // });
    this.usersSignal.update(users => users.filter(u => u.id !== id));
  }
}