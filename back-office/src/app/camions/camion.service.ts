import { Injectable, signal, inject } from '@angular/core';
import { Camion } from './camions.component';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class CamionService {
  private camionsSignal = signal<Camion[]>([]);

  readonly camions = this.camionsSignal.asReadonly();

  private http = inject(HttpClient);
  
  private apiUrl = 'http://localhost:8080/api/trucks';

  loadCamions(): Observable<Camion[]> {
    return this.http.get<Camion[]>(this.apiUrl).pipe(
      tap((data: Camion[]) => this.camionsSignal.set(data))
    );
  }

  ajouterCamion(camion: Omit<Camion, 'id_camion'>): Observable<Camion> {
    return this.http.post<Camion>(this.apiUrl, camion).pipe(
      tap((newCam: Camion) => {
        this.camionsSignal.update((camions: Camion[]) => [...camions, newCam]);
      })
    );
  }

  editerCamion(camionModifie: Camion): Observable<Camion> {
    return this.http.put<Camion>(`${this.apiUrl}/${camionModifie.id_camion}`, camionModifie).pipe(
      tap((updatedCam: Camion) => {
        this.camionsSignal.update((camions: Camion[]) => camions.map((c: Camion) => c.id_camion === updatedCam.id_camion ? updatedCam : c));
      })
    );
  }

  supprimerCamion(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(() => {
        this.camionsSignal.update((camions: Camion[]) => camions.filter((c: Camion) => c.id_camion !== id));
      })
    );
  }
}