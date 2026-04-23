import { Injectable, signal, inject } from '@angular/core';
import { User } from './user.model';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class UserService {
  private usersSignal = signal<User[]>([]);
  readonly users = this.usersSignal.asReadonly();

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/users';

  loadUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl).pipe(
      tap((data: User[]) => this.usersSignal.set(data))
    );
  }

  ajouterUser(user: Omit<User, 'id'>): Observable<User> {
    return this.http.post<User>(this.apiUrl, user).pipe(
      tap((newUser: User) => {
        this.usersSignal.update(users => [...users, newUser]);
      })
    );
  }

  editerUser(user: User): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${user.id}`, user).pipe(
      tap((updated: User) => {
        this.usersSignal.update(users => users.map(u => u.id === updated.id ? updated : u));
      })
    );
  }

  supprimerUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(() => {
        this.usersSignal.update(users => users.filter(u => u.id !== id));
      })
    );
  }
}
