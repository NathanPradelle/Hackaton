import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CamionService } from './camion.service';

export interface Camion {
  id_camion: number;
  id_modele: number;
  statut: string;
  plaque_immatriculation: string;
  quantite_essence: number;
}

@Component({
  selector: 'app-camions',
  imports: [RouterLink, FormsModule],
  templateUrl: './camions.component.html'
})
export class CamionsComponent {
  private camionService = inject(CamionService);
  camions = this.camionService.camions; // On se branche au service

  isAdding = signal(false);
  nouveauCamion = signal<Partial<Camion>>({});

  ajouter() {
    const camion = this.nouveauCamion() as Camion;
    if (camion.plaque_immatriculation && camion.statut) {
      // En attendant l'API, on génère un faux ID aléatoire
      camion.id_camion = Math.floor(Math.random() * 1000) + 4; 
      
      this.camionService.ajouterCamion(camion);
      
      // On reset le formulaire et on le ferme
      this.isAdding.set(false);
      this.nouveauCamion.set({});
    }
  }
}