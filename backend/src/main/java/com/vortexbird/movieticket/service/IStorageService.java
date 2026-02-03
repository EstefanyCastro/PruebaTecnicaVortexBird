package com.vortexbird.movieticket.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Interface for Storage Service operations.
 * 
 * Provides methods for uploading and deleting files in cloud storage.
 */
public interface IStorageService {
    
    /**
     * Upload a file to cloud storage.
     * 
     * @param file The file to upload
     * @return The public URL of the uploaded file
     * @throws RuntimeException if upload fails
     */
    String uploadFile(MultipartFile file);
    
    /**
     * Delete a file from cloud storage.
     * 
     * @param fileUrl The URL of the file to delete
     * @throws RuntimeException if deletion fails
     */
    void deleteFile(String fileUrl);
}
