import { Component, inject, signal, computed } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { CommandeService } from './commande.service';
import { ToastService } from '../toast/toast.service';
import { ToastComponent } from '../toast/toast.component';
import { ConfirmationService } from '../shared/confirmation-modal/confirmation.service';
import { ConfirmationModalComponent } from '../shared/confirmation-modal/confirmation-modal.component';
import { UserService } from '../users/user.service';

export interface Commande {
  id_commande: number;
  id_client: number;
  id_tournee?: number;
  adresse_texte: string;
  date_voulu: string;
  plage_horaire: string;
  prix: number;
  quantite: number;
  statut: string;
}

@Component({
  selector: 'app-commandes',
  imports: [RouterLink, FormsModule, ToastComponent, ConfirmationModalComponent, CurrencyPipe, DatePipe],
  templateUrl: './commandes.component.html'
})
export class CommandesComponent {
  private commandeService = inject(CommandeService);
  private toastService = inject(ToastService);
  private confirmationService = inject(ConfirmationService);
  private userService = inject(UserService);
  commandes = this.commandeService.commandes;
  clients = computed(() => this.userService.users().filter(u => u.role === 'CLIENT'));
  usersMap = computed(() => {
    const map = new Map<number, string>();
    this.userService.users().forEach(u => map.set(u.id_user, u.identifiant));
    return map;
  });

  searchTerm = signal('');
  sortColumn = signal<keyof Commande | ''>('');
  sortDirection = signal<'asc' | 'desc'>('asc');

  commandesFiltres = computed(() => {
    const term = this.searchTerm().toLowerCase();
    let result = this.commandes().filter(c => 
      c.adresse_texte.toLowerCase().includes(term) ||
      c.statut.toLowerCase().includes(term) ||
      c.id_client.toString().includes(term)
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

  sortBy(col: keyof Commande) {
    if (this.sortColumn() === col) {
      this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortColumn.set(col);
      this.sortDirection.set('asc');
    }
  }

  isAdding = signal(false);
  nouvelleCommande = signal<Partial<Commande>>({});

  ajouter() {
    const commande = this.nouvelleCommande() as Commande;
    if (commande.id_client && commande.adresse_texte) {
      if (commande.id_commande) {
        this.commandeService.editerCommande(commande);
      } else {
        commande.id_commande = Math.floor(Math.random() * 1000) + 103;
        this.commandeService.ajouterCommande(commande);
      }
      this.toastService.show('Commande enregistrée avec succès');
      
      this.isAdding.set(false);
      this.nouvelleCommande.set({});
    }
  }

  editer(commande: Commande) {
    this.nouvelleCommande.set({ ...commande });
    this.isAdding.set(true);
  }

  supprimer(id: number) {
    this.confirmationService.show('Êtes-vous sûr de vouloir supprimer cette commande ?', () => {
      this.commandeService.supprimerCommande(id);
      this.toastService.show('Commande supprimée', 'success');
    });
  }
}