import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { UserService } from './user.service';
import { User } from './user.model';
import { ToastService } from '../toast/toast.service';
import { ToastComponent } from '../toast/toast.component';
import { ConfirmationService } from '../shared/confirmation-modal/confirmation.service';
import { ConfirmationModalComponent } from '../shared/confirmation-modal/confirmation-modal.component';

@Component({
  selector: 'app-users',
  imports: [RouterLink, FormsModule, ToastComponent, ConfirmationModalComponent],
  templateUrl: './users.component.html'
})
export class UsersComponent implements OnInit {
  private userService = inject(UserService);
  private toastService = inject(ToastService);
  private confirmationService = inject(ConfirmationService);
  users = this.userService.users;

  ngOnInit(): void {
    this.userService.loadUsers().subscribe({
      error: () => this.toastService.show('Erreur lors du chargement des utilisateurs.', 'error')
    });
  }

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
    if (user.identifiant && user.role && (user.id || user.motDePasse)) {
      if (user.id) {
        this.userService.editerUser(user).subscribe({
          next: () => {
            this.toastService.show('Utilisateur modifié avec succès');
            this.isAdding.set(false);
            this.nouveauUser.set({});
          },
          error: () => this.toastService.show('Erreur lors de la modification', 'error')
        });
      } else {
        this.userService.ajouterUser(user).subscribe({
          next: () => {
            this.toastService.show('Utilisateur ajouté avec succès');
            this.isAdding.set(false);
            this.nouveauUser.set({});
          },
          error: () => this.toastService.show('Erreur lors de la création', 'error')
        });
      }
    }
  }

  editer(user: User) {
    this.nouveauUser.set({ ...user });
    this.isAdding.set(true);
  }

  supprimer(id: number) {
    this.confirmationService.show('Êtes-vous sûr de vouloir supprimer cet utilisateur ?', () => {
      this.userService.supprimerUser(id).subscribe({
        next: () => this.toastService.show('Utilisateur supprimé', 'success'),
        error: () => this.toastService.show('Erreur lors de la suppression', 'error')
      });
    });
  }
}
