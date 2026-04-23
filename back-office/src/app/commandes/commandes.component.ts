import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { CommandeService } from './commande.service';
import { ToastService } from '../toast/toast.service';
import { ToastComponent } from '../toast/toast.component';
import { ConfirmationService } from '../shared/confirmation-modal/confirmation.service';
import { ConfirmationModalComponent } from '../shared/confirmation-modal/confirmation-modal.component';
import { UserService } from '../users/user.service';

// ⚠️ INTERFACE MISE À JOUR POUR MATCHER LE BACKEND (OrderDto)
export interface Commande {
  id: number;
  clientId: number;
  tripId?: number;
  addressText: string;
  latitude?: number;  // Ajouté depuis le back
  longitude?: number; // Ajouté depuis le back
  requestedDate: string;
  timeSlot: string;
  price: number;
  quantity: number;
  status: string;
}

@Component({
  selector: 'app-commandes',
  imports: [RouterLink, FormsModule, ToastComponent, ConfirmationModalComponent, CurrencyPipe, DatePipe],
  templateUrl: './commandes.component.html'
})
export class CommandesComponent implements OnInit {
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

  // ⚠️ MISE À JOUR DES NOMS DE VARIABLES POUR LE FILTRE
  commandesFiltres = computed(() => {
    const term = this.searchTerm().toLowerCase();
    let result = this.commandes().filter(c => 
      c.addressText?.toLowerCase().includes(term) ||
      c.status?.toLowerCase().includes(term) ||
      c.clientId?.toString().includes(term)
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

  // ⚠️ CHARGEMENT DES DONNÉES AU DÉMARRAGE
  ngOnInit(): void {
    this.commandeService.loadCommandes().subscribe({
      error: () => this.toastService.show('Erreur lors du chargement des commandes.', 'error')
    });
  }

  // ⚠️ MISE À JOUR DE LA LOGIQUE DE SAUVEGARDE AVEC HTTP
  ajouter() {
    const commandeData = this.nouvelleCommande();
    
    if (!commandeData.clientId || !commandeData.addressText) {
      this.toastService.show('Veuillez remplir le client et l\'adresse.', 'error');
      return;
    }

    const onSave = {
      next: () => {
        this.toastService.show('Commande enregistrée avec succès', 'success');
        this.isAdding.set(false);
        this.nouvelleCommande.set({});
      },
      error: (err: any) => this.toastService.show('Erreur de sauvegarde', 'error')
    };

    if (commandeData.id) {
      // Édition
      this.commandeService.editerCommande(commandeData as Commande).subscribe(onSave);
    } else {
      // Ajout
      const { id, ...newCmd } = commandeData;
      this.commandeService.ajouterCommande(newCmd as Omit<Commande, 'id'>).subscribe(onSave);
    }
  }

  editer(commande: Commande) {
    this.nouvelleCommande.set({ ...commande });
    this.isAdding.set(true);
  }

  supprimer(id: number) {
    this.confirmationService.show('Êtes-vous sûr de vouloir supprimer cette commande ?', () => {
      this.commandeService.supprimerCommande(id).subscribe({
        next: () => this.toastService.show('Commande supprimée', 'success'),
        error: () => this.toastService.show('Erreur lors de la suppression', 'error')
      });
    });
  }
}