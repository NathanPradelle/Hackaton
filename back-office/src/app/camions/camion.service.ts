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

  ajouterCamion(camion: Omit<Camion, 'id'>): Observable<Camion> {
    return this.http.post<Camion>(this.apiUrl, camion).pipe(
      tap((newCam: Camion) => {
        this.camionsSignal.update(camions => [...camions, newCam]);
      })
    );
  }

  editerCamion(camionModifie: Camion): Observable<Camion> {
    // Note: Le backend attend probablement un PUT sur /api/trucks/{id}
    return this.http.put<Camion>(`${this.apiUrl}/${camionModifie.id}`, camionModifie).pipe(
      tap((updatedCam: Camion) => {
        this.camionsSignal.update(camions => 
          camions.map(c => c.id === updatedCam.id ? updatedCam : c)
        );
      })
    );
  }

  supprimerCamion(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(() => {
        this.camionsSignal.update(camions => camions.filter(c => c.id !== id));
      })
    );
  }
}