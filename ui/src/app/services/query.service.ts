import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";

const httpOptions = {
  headers : new HttpHeaders({'Content-Type' : 'application/json'})
};

@Injectable({
  providedIn: 'root'
})
export class QueryService {

  constructor(private http:HttpClient) { }

  get() {
    return this.http.get('/query');
  }

  add(body:string) {
    return this.http.post('/add', body, httpOptions)
  }
}
