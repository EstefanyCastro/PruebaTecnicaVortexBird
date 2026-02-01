/**
 * Movie model representing a movie entity.
 * Contains information about movies available in the system.
 */
export interface Movie {
  id: number;
  title: string;
  description: string;
  imageUrl: string;
  duration: number;
  genre: string;
  price: number;
  isEnabled: boolean;
}

/**
 * DTO for creating a movie.
 */
export interface CreateMovieRequest {
  title: string;
  description: string;
  imageUrl: string;
  duration: number;
  genre: string;
  price: number;
}
