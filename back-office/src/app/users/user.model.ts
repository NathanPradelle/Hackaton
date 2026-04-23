export interface User {
  id: number;           // Était 'id_user'
  identifier: string;    // Était 'identifiant'
  password: string;      // Était 'mot_de_passe'
  role: 'ADMIN' | 'CHAUFFEUR' | 'CLIENT';
}