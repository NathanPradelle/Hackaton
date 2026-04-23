import { Injectable, signal, inject } from '@angular/core';
import { Camion } from './camions.component';
// import { HttpClient } from '@angular/common/http';
// import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CamionService {
  private camionsSignal = signal<Camion[]>([
    { id_camion: 1, id_modele: 101, statut: 'Disponible', plaque_immatriculation: 'AB-123-CD', quantite_essence: 80 },
    { id_camion: 2, id_modele: 102, statut: 'En tournée', plaque_immatriculation: 'EF-456-GH', quantite_essence: 45 },
    { id_camion: 3, id_modele: 101, statut: 'En maintenance', plaque_immatriculation: 'IJ-789-KL', quantite_essence: 10 },
  ]);

  readonly camions = this.camionsSignal.asReadonly();

  // private http = inject(HttpClient);
  // private apiUrl = `${environment.apiUrl}/camions`;

  /*
  chargerCamions() {
    this.http.get<Camion[]>(this.apiUrl).subscribe(data => this.camionsSignal.set(data));
  }
  */

  ajouterCamion(camion: Camion) {
    // this.http.post<Camion>(this.apiUrl, camion).subscribe(newCam => {
    //   this.camionsSignal.update(camions => [...camions, newCam]);
    // });
    this.camionsSignal.update(camions => [...camions, camion]);
  }

  editerCamion(camionModifie: Camion) {
    // this.http.put<Camion>(`${this.apiUrl}/${camionModifie.id_camion}`, camionModifie).subscribe(updatedCam => {
    //   this.camionsSignal.update(camions => camions.map(c => c.id_camion === updatedCam.id_camion ? updatedCam : c));
    // });
    this.camionsSignal.update(camions =>
      camions.map(c => c.id_camion === camionModifie.id_camion ? camionModifie : c)
    );
  }

  supprimerCamion(id: number) {
    // this.http.delete(`${this.apiUrl}/${id}`).subscribe(() => {
    //   this.camionsSignal.update(camions => camions.filter(c => c.id_camion !== id));
    // });
    this.camionsSignal.update(camions => camions.filter(c => c.id_camion !== id));
  }
}