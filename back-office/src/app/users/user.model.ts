export interface User {
  id: number;
  identifiant: string;
  role: 'admin' | 'chauffeur' | 'client';
  motDePasse?: string;
}
