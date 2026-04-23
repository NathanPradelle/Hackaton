import { Component, inject, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CamionService } from './camion.service';
import { ToastService } from '../toast/toast.service';
import { ToastComponent } from '../toast/toast.component';
import { ConfirmationService } from '../shared/confirmation-modal/confirmation.service';
import { ConfirmationModalComponent } from '../shared/confirmation-modal/confirmation-modal.component';
import { ModeleService } from '../shared/modele.service';
import { Modele } from '../shared/modele';

export interface Camion {
  id_camion: number;
  id_modele: number;
  statut: string;
  plaque_immatriculation: string;
  quantite_essence: number;
}

@Component({
  selector: 'app-camions',
  imports: [RouterLink, FormsModule, ToastComponent, ConfirmationModalComponent],
  templateUrl: './camions.component.html'
})
export class CamionsComponent {
  private camionService = inject(CamionService);
  private toastService = inject(ToastService);
  private confirmationService = inject(ConfirmationService);
  private modeleService = inject(ModeleService);
  camions = this.camionService.camions; // On se branche au service
  modeles = this.modeleService.modeles;
  modelesMap = computed(() => {
    const map = new Map<number, string>();
    this.modeles().forEach(m => map.set(m.id_modele, `${m.marque} ${m.nom_modele}`));
    return map;
  });

  searchTerm = signal('');
  sortColumn = signal<keyof Camion | ''>('');
  sortDirection = signal<'asc' | 'desc'>('asc');

  camionsFiltres = computed(() => {
    const term = this.searchTerm().toLowerCase();
    let result = this.camions().filter(c => 
      c.plaque_immatriculation.toLowerCase().includes(term) ||
      c.statut.toLowerCase().includes(term)
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

  sortBy(col: keyof Camion) {
    if (this.sortColumn() === col) {
      this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortColumn.set(col);
      this.sortDirection.set('asc');
    }
  }

  isAdding = signal(false);
  nouveauCamion = signal<Partial<Camion>>({});

  ajouter() {
    const camion = this.nouveauCamion() as Camion;
    if (camion.plaque_immatriculation && camion.statut) {
      if (camion.id_camion) {
        this.camionService.editerCamion(camion);
      } else {
        camion.id_camion = Math.floor(Math.random() * 1000) + 4; 
        this.camionService.ajouterCamion(camion);
      }
      this.toastService.show('Camion enregistré avec succès');
      
      // On reset le formulaire et on le ferme
      this.isAdding.set(false);
      this.nouveauCamion.set({});
    }
  }

  editer(camion: Camion) {
    this.nouveauCamion.set({ ...camion }); // On copie pour ne pas modifier le tableau en direct
    this.isAdding.set(true);
  }

  supprimer(id: number) {
    this.confirmationService.show('Êtes-vous sûr de vouloir supprimer ce camion ?', () => {
      this.camionService.supprimerCamion(id);
      this.toastService.show('Camion supprimé', 'success');
    });
  }
}