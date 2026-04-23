import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // On récupère le token depuis le localStorage
  const authToken = localStorage.getItem('token');

  // Si un token est présent, on clone la requête pour y ajouter le header d'autorisation
  if (authToken) {
    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${authToken}`
      }
    });

    return next(authReq);
  }
  
  // Sinon, on laisse passer la requête telle quelle
  return next(req);
};