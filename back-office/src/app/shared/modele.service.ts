import { Injectable, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Modele } from './modele';

@Injectable({ providedIn: 'root' })
export class ModeleService {
  private modelesSignal = signal<Modele[]>([]);

  readonly modeles = this.modelesSignal.asReadonly();

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/models';

  loadModeles(): Observable<Modele[]> {
    return this.http.get<Modele[]>(this.apiUrl).pipe(
      tap((data: Modele[]) => this.modelesSignal.set(data))
    );
  }
}