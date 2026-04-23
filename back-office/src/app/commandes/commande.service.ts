import { Injectable, signal, inject } from '@angular/core';
import { Commande } from './commandes.component';
// import { HttpClient } from '@angular/common/http';
// import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CommandeService {
  private commandesSignal = signal<Commande[]>([
    { id_commande: 101, id_client: 3, id_tournee: 1, adresse_texte: '10 rue de la Logistique, Paris', date_voulu: '2026-05-01', plage_horaire: '08:00 - 12:00', prix: 150.50, quantite: 50, statut: 'En préparation' },
    { id_commande: 102, id_client: 4, adresse_texte: 'ZAC des Entrepôts, Lyon', date_voulu: '2026-05-02', plage_horaire: '14:00 - 18:00', prix: 320.00, quantite: 120, statut: 'En attente' },
  ]);

  readonly commandes = this.commandesSignal.asReadonly();

  // private http = inject(HttpClient);
  // private apiUrl = `${environment.apiUrl}/commandes`;

  /* chargerCommandes() { ... } */

  ajouterCommande(commande: Commande) {
    // this.http.post<Commande>(this.apiUrl, commande).subscribe(newCmd => {
    //   this.commandesSignal.update(commandes => [...commandes, newCmd]);
    // });
    this.commandesSignal.update(commandes => [...commandes, commande]);
  }

  editerCommande(commandeModifiee: Commande) {
    // this.http.put<Commande>(`${this.apiUrl}/${commandeModifiee.id_commande}`, commandeModifiee).subscribe(updatedCmd => {
    //   this.commandesSignal.update(commandes => commandes.map(c => c.id_commande === updatedCmd.id_commande ? updatedCmd : c));
    // });
    this.commandesSignal.update(commandes =>
      commandes.map(c => c.id_commande === commandeModifiee.id_commande ? commandeModifiee : c)
    );
  }

  supprimerCommande(id: number) {
    // this.http.delete(`${this.apiUrl}/${id}`).subscribe(() => {
    //   this.commandesSignal.update(commandes => commandes.filter(c => c.id_commande !== id));
    // });
    this.commandesSignal.update(commandes => commandes.filter(c => c.id_commande !== id));
  }
}