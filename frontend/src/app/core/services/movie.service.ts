import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Movie, CreateMovieRequest } from '@models/movie.model';
import { ApiResponse } from '@models/api-response.model';


/**
 * Service for managing movie-related API operations.
 * 
 * Handles all HTTP requests related to movie management including
 * creation, retrieval, updating, and deletion.
 */
@Injectable({
  providedIn: 'root'
})
export class MovieService {
  private apiUrl = '/api/movies';

  constructor(private http: HttpClient) { }

  getAllMovies(): Observable<ApiResponse<Movie[]>> {
    return this.http.get<ApiResponse<Movie[]>>(this.apiUrl);
  }

  searchMovies(title?: string, genre?: string): Observable<ApiResponse<Movie[]>> {
    let params: any = {};
    if (title) params.title = title;
    if (genre) params.genre = genre;
    return this.http.get<ApiResponse<Movie[]>>(this.apiUrl, { params });
  }

  getMovieById(id: number): Observable<ApiResponse<Movie>> {
    return this.http.get<ApiResponse<Movie>>(`${this.apiUrl}/${id}`);
  }

  createMovieWithImage(formData: FormData): Observable<ApiResponse<Movie>> {
    return this.http.post<ApiResponse<Movie>>(this.apiUrl, formData);
  }

  updateMovieWithImage(id: number, formData: FormData): Observable<ApiResponse<Movie>> {
    return this.http.put<ApiResponse<Movie>>(`${this.apiUrl}/${id}`, formData);
  }

  disableMovie(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
  }
}
