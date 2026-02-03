package com.vortexbird.movieticket.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for S3StorageService.
 * 
 * Tests follow the AAA pattern:
 * - Arrange: Setup test data and mocks
 * - Act: Execute the method under test
 * - Assert: Verify the results
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("S3StorageService Tests")
class S3StorageServiceTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3StorageService s3StorageService;

    @Mock
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        // Set required fields using reflection
        ReflectionTestUtils.setField(s3StorageService, "bucketName", "test-bucket");
        ReflectionTestUtils.setField(s3StorageService, "region", "us-east-1");
        ReflectionTestUtils.setField(s3StorageService, "s3Client", s3Client);
    }

    @Test
    @DisplayName("Should upload file successfully")
    void testUploadFile_Success() throws IOException {
        // Arrange
        when(mockFile.getOriginalFilename()).thenReturn("test-image.jpg");
        when(mockFile.getContentType()).thenReturn("image/jpeg");
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
        
        // Act
        String result = s3StorageService.uploadFile(mockFile);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("test-bucket"));
        assertTrue(result.contains("movies/"));
        assertTrue(result.endsWith(".jpg"));
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("Should throw exception when file is null")
    void testUploadFile_NullFile() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            s3StorageService.uploadFile(null);
        });
        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("Should throw exception when file is empty")
    void testUploadFile_EmptyFile() {
        // Arrange
        when(mockFile.isEmpty()).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            s3StorageService.uploadFile(mockFile);
        });
        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("Should throw exception when file size exceeds limit")
    void testUploadFile_FileSizeExceeded() throws IOException {
        // Arrange
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getSize()).thenReturn(6 * 1024 * 1024L); // 6MB (exceeds 5MB limit)

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            s3StorageService.uploadFile(mockFile);
        });
        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("Should throw exception when content type is not allowed")
    void testUploadFile_InvalidContentType() {
        // Arrange
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getContentType()).thenReturn("application/pdf");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            s3StorageService.uploadFile(mockFile);
        });
        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("Should handle S3Exception during upload")
    void testUploadFile_S3Exception() throws IOException {
        // Arrange
        when(mockFile.getOriginalFilename()).thenReturn("test-image.jpg");
        when(mockFile.getContentType()).thenReturn("image/jpeg");
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
            .thenThrow(S3Exception.builder().message("S3 error").build());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            s3StorageService.uploadFile(mockFile);
        });
    }

    @Test
    @DisplayName("Should delete file successfully")
    void testDeleteFile_Success() {
        // Arrange
        String fileUrl = "https://test-bucket.s3.us-east-1.amazonaws.com/movies/test-image.jpg";
        // No need to mock deleteObject - just verify it's called

        // Act
        s3StorageService.deleteFile(fileUrl);

        // Assert
        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    @DisplayName("Should handle invalid file URL during delete")
    void testDeleteFile_InvalidUrl() {
        // Arrange
        String invalidUrl = "invalid-url";

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            s3StorageService.deleteFile(invalidUrl);
        });
    }

    @Test
    @DisplayName("Should handle S3Exception during delete")
    void testDeleteFile_S3Exception() {
        // Arrange
        String fileUrl = "https://test-bucket.s3.us-east-1.amazonaws.com/movies/test-image.jpg";
        doThrow(S3Exception.builder().message("S3 error").build())
            .when(s3Client).deleteObject(any(DeleteObjectRequest.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            s3StorageService.deleteFile(fileUrl);
        });
    }

    @Test
    @DisplayName("Should accept valid image content types")
    void testUploadFile_ValidContentTypes() throws IOException {
        // Test multiple valid content types
        String[] validTypes = {"image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"};
        
        for (String contentType : validTypes) {
            // Arrange
            when(mockFile.getOriginalFilename()).thenReturn("test." + contentType.split("/")[1]);
            when(mockFile.getContentType()).thenReturn(contentType);
            when(mockFile.getSize()).thenReturn(1024L);
            when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
            
            // Act
            String result = s3StorageService.uploadFile(mockFile);

            // Assert
            assertNotNull(result);
            assertTrue(result.contains("test-bucket"));
        }
        
        verify(s3Client, times(validTypes.length)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
}
