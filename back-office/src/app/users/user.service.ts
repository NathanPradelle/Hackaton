import { Injectable, signal, inject } from '@angular/core';
import { User } from './users.component';
// import { HttpClient } from '@angular/common/http';
// import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class UserService {
  private usersSignal = signal<User[]>([
    { id_user: 1, identifiant: 'admin_principal', role: 'ADMIN' },
    { id_user: 2, identifiant: 'jean_chauffeur', role: 'CHAUFFEUR' },
    { id_user: 3, identifiant: 'entreprise_dupont', role: 'CLIENT' },
  ]);

  readonly users = this.usersSignal.asReadonly();

  // private http = inject(HttpClient);
  // private apiUrl = `${environment.apiUrl}/users`;

  /* chargerUsers() { ... } */

  ajouterUser(user: User) {
    // this.http.post<User>(this.apiUrl, user).subscribe(newUser => {
    //   this.usersSignal.update(users => [...users, newUser]);
    // });
    this.usersSignal.update(users => [...users, user]);
  }

  editerUser(userModifie: User) {
    // this.http.put<User>(`${this.apiUrl}/${userModifie.id_user}`, userModifie).subscribe(updatedUser => {
    //   this.usersSignal.update(users => users.map(u => u.id_user === updatedUser.id_user ? updatedUser : u));
    // });
    this.usersSignal.update(users =>
      users.map(u => u.id_user === userModifie.id_user ? userModifie : u)
    );
  }

  supprimerUser(id: number) {
    // this.http.delete(`${this.apiUrl}/${id}`).subscribe(() => {
    //   this.usersSignal.update(users => users.filter(u => u.id_user !== id));
    // });
    this.usersSignal.update(users => users.filter(u => u.id_user !== id));
  }
}