import { Injectable, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Client } from './client.model';

@Injectable({ providedIn: 'root' })
export class ClientService {
  private clientsSignal = signal<Client[]>([]);
  readonly clients = this.clientsSignal.asReadonly();

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/clients';

  loadClients(): Observable<Client[]> {
    return this.http.get<Client[]>(this.apiUrl).pipe(
      tap((data: Client[]) => this.clientsSignal.set(data))
    );
  }
}
