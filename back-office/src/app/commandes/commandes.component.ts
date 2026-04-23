import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommandeService } from './commande.service';

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
  imports: [RouterLink],
  templateUrl: './commandes.component.html'
})
export class CommandesComponent {
  private commandeService = inject(CommandeService);
  commandes = this.commandeService.commandes;
}