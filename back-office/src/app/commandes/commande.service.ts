import { Injectable, signal, inject } from '@angular/core';
import { Commande } from './commandes.component';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class CommandeService {
  private commandesSignal = signal<Commande[]>([]);
  readonly commandes = this.commandesSignal.asReadonly();

  private http = inject(HttpClient);
  // On pointe vers le bon endpoint backend
  private apiUrl = 'http://localhost:8080/api/orders';

  loadCommandes(): Observable<Commande[]> {
    return this.http.get<Commande[]>(this.apiUrl).pipe(
      tap((data: Commande[]) => this.commandesSignal.set(data))
    );
  }

  ajouterCommande(commande: Omit<Commande, 'id'>): Observable<Commande> {
    return this.http.post<Commande>(this.apiUrl, commande).pipe(
      tap((newCmd: Commande) => {
        this.commandesSignal.update((commandes: Commande[]) => [...commandes, newCmd]);
      })
    );
  }

  editerCommande(commandeModifiee: Commande): Observable<Commande> {
    return this.http.put<Commande>(`${this.apiUrl}/${commandeModifiee.id}`, commandeModifiee).pipe(
      tap((updatedCmd: Commande) => {
        this.commandesSignal.update((commandes: Commande[]) => 
          commandes.map((c: Commande) => c.id === updatedCmd.id ? updatedCmd : c)
        );
      })
    );
  }

  supprimerCommande(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(() => {
        this.commandesSignal.update((commandes: Commande[]) => 
          commandes.filter((c: Commande) => c.id !== id)
        );
      })
    );
  }
}