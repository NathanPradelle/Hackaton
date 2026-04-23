import { Injectable, signal } from '@angular/core';
import { Modele } from './modele';

@Injectable({ providedIn: 'root' })
export class ModeleService {
  private modelesSignal = signal<Modele[]>([
    { id_modele: 101, marque: 'Renault', nom_modele: 'Trucks T' },
    { id_modele: 102, marque: 'Volvo', nom_modele: 'FH16' },
    { id_modele: 103, marque: 'Scania', nom_modele: 'R-series' },
  ]);

  readonly modeles = this.modelesSignal.asReadonly();
}