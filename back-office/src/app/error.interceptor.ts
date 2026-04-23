import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { ToastService } from './toast/toast.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const toastService = inject(ToastService);

  return next(req).pipe(
    catchError((error) => {
      console.error('Erreur HTTP interceptée:', error);
      const errorMsg = error.error?.message || error.message || 'Une erreur est survenue avec le serveur.';
      toastService.show(errorMsg, 'error');
      return throwError(() => error);
    })
  );
};