import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { TokenStorageService } from './token-storage.service';
import { User } from '../user/user';
import { headersToString } from 'selenium-webdriver/http';


const URL = environment.apiUrl;


interface LoginResult {
  token: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthApiService {


  constructor(
      private readonly http: HttpClient,
      private readonly token: TokenStorageService) { }

  login(username: string, password: string) {
    const body = new HttpParams()
    .set('grant_type', 'password')
    .set('username', username)
    .set('password', password);

    return this.http.post<LoginResult>(`http://localhost:8777/oauth/token`, body.toString(), {
                                                                              headers: new HttpHeaders()
                                                                              .append('Content-Type', 'application/x-www-form-urlencoded')
                                                                              .append('Authorization', 'Basic ' + btoa('ANG:POEI0002-2'))
                                                                            })
        .pipe(tap((res: LoginResult) => this.token.save(res.token)));
  }

  logout() {
    return this.http.post<void>(`${URL}/logout`, {})
        .pipe(tap(() => this.token.clear()));
  }

  me() {
    return this.http.get<User>(`${URL}/user/me`);
  }
}
