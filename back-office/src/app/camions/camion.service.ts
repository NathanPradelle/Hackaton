import { Injectable, signal } from '@angular/core';
import { Camion } from './camions.component';

@Injectable({ providedIn: 'root' })
export class CamionService {
  private camionsSignal = signal<Camion[]>([
    { id_camion: 1, id_modele: 101, statut: 'Disponible', plaque_immatriculation: 'AB-123-CD', quantite_essence: 80 },
    { id_camion: 2, id_modele: 102, statut: 'En tournée', plaque_immatriculation: 'EF-456-GH', quantite_essence: 45 },
    { id_camion: 3, id_modele: 101, statut: 'En maintenance', plaque_immatriculation: 'IJ-789-KL', quantite_essence: 10 },
  ]);

  readonly camions = this.camionsSignal.asReadonly();

  ajouterCamion(camion: Camion) {
    // Plus tard, ici il y aura l'appel POST vers ton API
    this.camionsSignal.update(camions => [...camions, camion]);
  }
}