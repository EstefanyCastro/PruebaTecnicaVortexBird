import { Injectable } from '@angular/core';

/**
 * Service for handling image file validations and preview generation.
 */
@Injectable({
  providedIn: 'root'
})
export class StorageService {

  validateImage(file: File): { valid: boolean; error?: string } {
    const maxSize = 5 * 1024 * 1024; // 5MB
    const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];

    if (!file) {
      return { valid: false, error: 'No se ha seleccionado ningún archivo' };
    }

    if (file.size > maxSize) {
      return { valid: false, error: 'El archivo excede el tamaño máximo de 5MB' };
    }

    if (!allowedTypes.includes(file.type)) {
      return { valid: false, error: 'Tipo de archivo no permitido. Use: JPG, PNG, GIF o WEBP' };
    }

    return { valid: true };
  }

  createImagePreview(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = (e: any) => resolve(e.target.result);
      reader.onerror = (error) => reject(error);
      reader.readAsDataURL(file);
    });
  }
}
