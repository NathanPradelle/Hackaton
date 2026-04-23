import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { CommandeService } from './commande.service';
import { ToastService } from '../toast/toast.service';
import { ToastComponent } from '../toast/toast.component';
import { ConfirmationService } from '../shared/confirmation-modal/confirmation.service';
import { ConfirmationModalComponent } from '../shared/confirmation-modal/confirmation-modal.component';
import { ClientService } from '../clients/client.service';

export interface Commande {
  id: number;
  clientId: number;
  tourneeId?: number;
  adresseTexte: string;
  latitude?: number;
  longitude?: number;
  dateVoulu: string;
  plageHoraire: string;
  prix: number;
  quantite: number;
  statut: string;
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
  private clientService = inject(ClientService);

  commandes = this.commandeService.commandes;
  clients = this.clientService.clients;

  clientsMap = computed(() => {
    const map = new Map<number, string>();
    this.clientService.clients().forEach(c => map.set(c.id, c.nom));
    return map;
  });

  searchTerm = signal('');
  sortColumn = signal<keyof Commande | ''>('');
  sortDirection = signal<'asc' | 'desc'>('asc');

  commandesFiltres = computed(() => {
    const term = this.searchTerm().toLowerCase();
    let result = this.commandes().filter(c =>
      c.adresseTexte?.toLowerCase().includes(term) ||
      c.statut?.toLowerCase().includes(term) ||
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

  ngOnInit(): void {
    this.commandeService.loadCommandes().subscribe({
      error: () => this.toastService.show('Erreur lors du chargement des commandes.', 'error')
    });
    this.clientService.loadClients().subscribe();
  }

  ajouter() {
    const commandeData = this.nouvelleCommande();

    if (!commandeData.clientId || !commandeData.adresseTexte) {
      this.toastService.show('Veuillez remplir le client et l\'adresse.', 'error');
      return;
    }

    const onSave = {
      next: () => {
        this.toastService.show('Commande enregistrée avec succès', 'success');
        this.isAdding.set(false);
        this.nouvelleCommande.set({});
      },
      error: () => this.toastService.show('Erreur de sauvegarde', 'error')
    };

    if (commandeData.id) {
      this.commandeService.editerCommande(commandeData as Commande).subscribe(onSave);
    } else {
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
