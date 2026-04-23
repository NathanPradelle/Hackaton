import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Ici, on récupèrera le token (ex: depuis le localStorage)
  const authToken = 'MON_FUTUR_TOKEN_JWT';

  // On clone la requête pour y ajouter le header d'autorisation
  const authReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${authToken}`
    }
  });

  // On passe la requête clonée à la suite de la chaîne d'intercepteurs
  console.log('Requête interceptée !', authReq);

  return next(authReq);
};