import { Injectable, signal } from '@angular/core';
import { Commande } from './commandes.component';

@Injectable({ providedIn: 'root' })
export class CommandeService {
  private commandesSignal = signal<Commande[]>([
    { id_commande: 101, id_client: 3, id_tournee: 1, adresse_texte: '10 rue de la Logistique, Paris', date_voulu: '2026-05-01', plage_horaire: '08:00 - 12:00', prix: 150.50, quantite: 50, statut: 'En préparation' },
    { id_commande: 102, id_client: 4, adresse_texte: 'ZAC des Entrepôts, Lyon', date_voulu: '2026-05-02', plage_horaire: '14:00 - 18:00', prix: 320.00, quantite: 120, statut: 'En attente' },
  ]);

  readonly commandes = this.commandesSignal.asReadonly();
}