import { Injectable, signal } from '@angular/core';
import { User } from './users.component';

@Injectable({ providedIn: 'root' })
export class UserService {
  private usersSignal = signal<User[]>([
    { id_user: 1, identifiant: 'admin_principal', role: 'ADMIN' },
    { id_user: 2, identifiant: 'jean_chauffeur', role: 'CHAUFFEUR' },
    { id_user: 3, identifiant: 'entreprise_dupont', role: 'CLIENT' },
  ]);

  readonly users = this.usersSignal.asReadonly();
}