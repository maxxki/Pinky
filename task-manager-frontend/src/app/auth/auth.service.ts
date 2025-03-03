import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, catchError, map, of, tap, throwError } from 'rxjs';
import { environment } from '../../environments/environment';

// Interface für die Login Response vom Backend
interface LoginResponse {
  token: string;
  role: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl + '/auth'; // Backend Auth API URL
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.isAuthenticated()); // Observable für Authentifizierungsstatus

  constructor(private http: HttpClient, private router: Router) { }

  // Observable für Authentifizierungsstatus (für Komponenten, die Statusänderungen beobachten müssen)
  isAuthenticated$: Observable<boolean> = this.isAuthenticatedSubject.asObservable();

  // Login Methode, sendet Credentials zum Backend und verarbeitet die Antwort
  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, { username, password }).pipe( // Post-Request an das Backend
      tap(response => { // Side-Effect: Bei erfolgreichem Login...
        localStorage.setItem('token', response.token); // ...Token im localStorage speichern
        localStorage.setItem('role', response.role);   // ...Rolle im localStorage speichern
        this.isAuthenticatedSubject.next(true);       // ...Authentifizierungsstatus auf true setzen
      }),
      catchError(error => { // Fehlerbehandlung mit RxJS catchError Operator
        console.error('Login fehlgeschlagen', error);   // ...Fehler in der Konsole protokollieren
        this.isAuthenticatedSubject.next(false);      // ...Authentifizierungsstatus auf false setzen
        return throwError(() => error);              // ...Fehler weiterwerfen, damit Komponente ihn behandeln kann
      })
    );
  }

  // Logout Methode, entfernt Token und Rolle aus dem localStorage und navigiert zum Login
  logout(): void {
    localStorage.removeItem('token'); // Token aus localStorage entfernen
    localStorage.removeItem('role');  // Rolle aus localStorage entfernen
    this.isAuthenticatedSubject.next(false); // Authentifizierungsstatus auf false setzen
    this.router.navigate(['/login']);    // Zum Login-Screen navigieren
  }

  // Gibt das JWT-Token aus dem localStorage zurück (oder null, falls nicht vorhanden)
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  // Gibt die Rolle des Nutzers aus dem localStorage zurück (oder null, falls nicht vorhanden)
  getRole(): string | null {
    return localStorage.getItem('role');
  }

  // Prüft, ob ein JWT-Token im localStorage vorhanden ist (indikativ für Authentifizierung)
  isAuthenticated(): boolean {
    return !!this.getToken(); // !! konvertiert String/null zu boolean (true wenn Token da, false wenn null)
  }

  // **Optionale Methode (später relevant):**  JWT-Token validieren (gegen Backend oder lokal)
  // validateToken(token: string): Observable<boolean> {
  //   // Implementierung der Token-Validierung (z.B. HTTP-Request zum Backend oder lokale JWT-Verifikation)
  //   return of(true); // Placeholder: Hier echte Validierung implementieren
  // }

  // **Optionale Methode (später relevant):**  Benutzernamen aus dem JWT-Token extrahieren (lokal, ohne Backend-Request)
  // getUsernameFromToken(token: string): string | null {
  //   // Implementierung der Username-Extraktion aus dem JWT-Token (z.B. via jwt-decode Bibliothek)
  //   return null; // Placeholder: Hier echte Extraktion implementieren
  // }
}
