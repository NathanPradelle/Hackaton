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
  modelId: number;
  status: string;
  licensePlate: string;
  currentFuelLevel: number;
}

@Component({
  selector: 'app-camions',
  imports: [RouterLink, FormsModule, ToastComponent, ConfirmationModalComponent],
  templateUrl: './camions.component.html'
})
export class CamionsComponent implements OnInit {
  private camionService = inject(CamionService);
  private toastService = inject(ToastService);
  private confirmationService = inject(ConfirmationService);
  private modeleService = inject(ModeleService);
  camions = this.camionService.camions; // On se branche au service
  modeles = this.modeleService.modeles;
  modelesMap = computed(() => {
    const map = new Map<number, string>();
    // J'assume que l'entité Modele a les propriétés: id, marque, nomModele
    this.modeles().forEach((m: Modele) => map.set(m.id, `${m.marque} ${m.nomModele}`));
    return map;
  });

  searchTerm = signal('');
  sortColumn = signal<keyof Camion | ''>('');
  sortDirection = signal<'asc' | 'desc'>('asc');

  camionsFiltres = computed(() => {
    const term = this.searchTerm().toLowerCase();
    let result = this.camions().filter((c: Camion) => 
      c.licensePlate?.toLowerCase().includes(term) ||
      c.status.toLowerCase().includes(term)
    );

    const col = this.sortColumn();
    if (col) {
      result.sort((a: Camion, b: Camion) => {
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

  ngOnInit(): void {
    // On charge les données initiales au démarrage du composant.
    this.camionService.loadCamions().subscribe({
      error: () => this.toastService.show('Erreur lors du chargement des camions.', 'error')
    });
    
    this.modeleService.loadModeles().subscribe({
      error: () => this.toastService.show('Erreur lors du chargement des modèles.', 'error')
    });
  }

  ajouter() {
    const camionData = this.nouveauCamion();
    if (!camionData.licensePlate || !camionData.status || !camionData.modelId) {
      this.toastService.show('Veuillez remplir tous les champs obligatoires.', 'error');
      return;
    }

    const onSave = {
      next: () => {
        this.toastService.show('Camion enregistré avec succès');
        this.isAdding.set(false);
        this.nouveauCamion.set({});
      },
      error: (err: any) => this.toastService.show(`Erreur: ${err.message || 'Le serveur a rencontré un problème.'}`, 'error')
    };

    if (camionData.id) {
      // Mode édition
      this.camionService.editerCamion(camionData as Camion).subscribe(onSave);
    } else {
      // Mode ajout : on s'assure de ne pas envoyer d'ID au backend
      const { id, ...newCamion } = camionData;
      this.camionService.ajouterCamion(newCamion as Omit<Camion, 'id'>).subscribe(onSave);
    }
  }

  editer(camion: Camion) {
    this.nouveauCamion.set({ ...camion }); // On copie pour ne pas modifier le tableau en direct
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