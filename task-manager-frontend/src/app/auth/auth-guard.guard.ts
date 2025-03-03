import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from './auth.service';
import { Observable, map, take } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) { }

  // CanActivate Guard Methode, wird vor dem Zugriff auf eine Route aufgerufen
  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.authService.isAuthenticated$.pipe( // Observable für Authentifizierungsstatus abonnieren
      take(1), // Nur den aktuellen Statuswert nehmen (und Observable abschließen)
      map(isAuthenticated => { // Wert des Authentifizierungsstatus transformieren
        if (isAuthenticated) { // Wenn authentifiziert, Route erlauben
          return true;
        } else { // Wenn nicht authentifiziert...
          this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } }); // ...zum Login umleiten, Return-URL mitgeben
          return false; // Route verweigern
        }
      })
    );
  }
}
