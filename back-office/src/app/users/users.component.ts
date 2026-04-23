import { Component, inject, signal, computed } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { UserService } from './user.service';
import { ToastService } from '../toast/toast.service';
import { ToastComponent } from '../toast/toast.component';
import { ConfirmationService } from '../shared/confirmation-modal/confirmation.service';
import { ConfirmationModalComponent } from '../shared/confirmation-modal/confirmation-modal.component';

export interface User {
  id_user: number;
  identifiant: string;
  role: string;
  nom?: string;
  prenom?: string;
  numero_permis?: string;
  numero_siret?: string;
  ville?: string;
}

@Component({
  selector: 'app-users',
  imports: [RouterLink, FormsModule, ToastComponent, ConfirmationModalComponent],
  templateUrl: './users.component.html'
})
export class UsersComponent {
  private userService = inject(UserService);
  private toastService = inject(ToastService);
  private confirmationService = inject(ConfirmationService);
  users = this.userService.users;

  searchTerm = signal('');
  sortColumn = signal<keyof User | ''>('');
  sortDirection = signal<'asc' | 'desc'>('asc');

  usersFiltres = computed(() => {
    const term = this.searchTerm().toLowerCase();
    let result = this.users().filter(u => 
      u.identifiant.toLowerCase().includes(term) ||
      u.role.toLowerCase().includes(term)
    );

    const col = this.sortColumn();
    if (col) {
      result.sort((a, b) => {
        const valA = a[col];
        const valB = b[col];
        if (valA === valB) return 0;
        if (valA == null) return 1;
        if (valB == null) return -1;
        if (valA < valB) return this.sortDirection() === 'asc' ? -1 : 1;
        return this.sortDirection() === 'asc' ? 1 : -1;
      });
    }
    return result;
  });

  sortBy(col: keyof User) {
    if (this.sortColumn() === col) {
      this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortColumn.set(col);
      this.sortDirection.set('asc');
    }
  }

  isAdding = signal(false);
  nouveauUser = signal<Partial<User>>({});

  ajouter() {
    const user = this.nouveauUser() as User;
    if (user.identifiant && user.role) {
      // Nettoyage des champs inutiles selon le rôle
      if (user.role === 'ADMIN') {
        delete user.nom; delete user.prenom; delete user.numero_permis; delete user.numero_siret; delete user.ville;
      } else if (user.role === 'CHAUFFEUR') {
        delete user.numero_siret; delete user.ville;
      } else if (user.role === 'CLIENT') {
        delete user.prenom; delete user.numero_permis;
      }

      if (user.id_user) {
        this.userService.editerUser(user);
      } else {
        user.id_user = Math.floor(Math.random() * 1000) + 4;
        this.userService.ajouterUser(user);
      }
      this.toastService.show('Utilisateur enregistré avec succès');
      
      this.isAdding.set(false);
      this.nouveauUser.set({});
    }
  }

  editer(user: User) {
    this.nouveauUser.set({ ...user });
    this.isAdding.set(true);
  }

  supprimer(id: number) {
    this.confirmationService.show('Êtes-vous sûr de vouloir supprimer cet utilisateur ?', () => {
      this.userService.supprimerUser(id);
      this.toastService.show('Utilisateur supprimé', 'success');
    });
  }
}