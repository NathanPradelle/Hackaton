import { Component, inject, signal, computed, OnInit } from '@angular/core';
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
  id: number;
  modeleId: number;
  statut: string;
  plaqueImmatriculation: string;
  quantiteEssence: number;
}

@Component({
  selector: 'app-camions',
  standalone: true,
  imports: [RouterLink, FormsModule, ToastComponent, ConfirmationModalComponent],
  templateUrl: './camions.component.html'
})
export class CamionsComponent implements OnInit {
  private camionService = inject(CamionService);
  private toastService = inject(ToastService);
  private confirmationService = inject(ConfirmationService);
  private modeleService = inject(ModeleService);

  camions = this.camionService.camions;
  modeles = this.modeleService.modeles;

  modelesMap = computed(() => {
    const map = new Map<number, string>();
    this.modeles().forEach((m: Modele) => map.set(m.id, `${m.marque} ${m.nomModele}`));
    return map;
  });

  searchTerm = signal('');
  sortColumn = signal<keyof Camion | ''>('');
  sortDirection = signal<'asc' | 'desc'>('asc');

  camionsFiltres = computed(() => {
    const term = this.searchTerm().toLowerCase();
    let result = this.camions().filter((c: Camion) =>
      c.plaqueImmatriculation?.toLowerCase().includes(term) ||
      c.statut?.toLowerCase().includes(term)
    );

    const col = this.sortColumn();
    if (col) {
      result.sort((a: Camion, b: Camion) => {
        const valA = a[col];
        const valB = b[col];
        if (valA === valB) return 0;
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

  ngOnInit(): void {
    this.camionService.loadCamions().subscribe({
      error: () => this.toastService.show('Erreur lors du chargement des camions.', 'error')
    });

    this.modeleService.loadModeles().subscribe({
      error: () => this.toastService.show('Erreur lors du chargement des modèles.', 'error')
    });
  }

  ajouter() {
    const camionData = this.nouveauCamion();
    if (!camionData.plaqueImmatriculation || !camionData.statut || !camionData.modeleId) {
      this.toastService.show('Veuillez remplir tous les champs obligatoires.', 'error');
      return;
    }

    const onSave = {
      next: () => {
        this.toastService.show('Camion enregistré avec succès');
        this.isAdding.set(false);
        this.nouveauCamion.set({});
      },
      error: () => this.toastService.show('Erreur de sauvegarde serveur.', 'error')
    };

    if (camionData.id) {
      this.camionService.editerCamion(camionData as Camion).subscribe(onSave);
    } else {
      const { id, ...newCamion } = camionData;
      this.camionService.ajouterCamion(newCamion as Omit<Camion, 'id'>).subscribe(onSave);
    }
  }

  editer(camion: Camion) {
    this.nouveauCamion.set({ ...camion });
    this.isAdding.set(true);
  }

  supprimer(id: number) {
    this.confirmationService.show('Êtes-vous sûr de vouloir supprimer ce camion ?', () => {
      this.camionService.supprimerCamion(id).subscribe({
        next: () => this.toastService.show('Camion supprimé', 'success'),
        error: () => this.toastService.show('Erreur lors de la suppression', 'error')
      });
    });
  }
}
