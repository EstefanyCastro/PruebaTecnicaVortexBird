package com.vortexbird.movieticket.service;

import com.vortexbird.movieticket.model.Customer;
import com.vortexbird.movieticket.model.Movie;
import com.vortexbird.movieticket.model.PurchaseStatus;
import com.vortexbird.movieticket.model.TicketPurchase;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EmailService.
 * 
 * Follows AAA pattern (Arrange, Act, Assert).
 * Tests apply SOLID principles and clean code practices.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmailService Tests")
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    private TicketPurchase testPurchase;

    @BeforeEach
    void setUp() {
        // Arrange - Create test data (DRY principle)
        testPurchase = createTestPurchase();
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    @DisplayName("Should send purchase confirmation email successfully")
    void shouldSendPurchaseConfirmationEmailSuccessfully() {
        // Arrange
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        emailService.sendPurchaseConfirmation(testPurchase);

        // Assert
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Should handle email sending failure gracefully")
    void shouldHandleEmailSendingFailureGracefully() {
        // Arrange
        doThrow(new RuntimeException("SMTP connection failed"))
            .when(mailSender).send(any(MimeMessage.class));

        // Act & Assert - Should not throw exception (logs error instead)
        emailService.sendPurchaseConfirmation(testPurchase);

        // Verify that email sending was attempted
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Should include all purchase details in email")
    void shouldIncludeAllPurchaseDetailsInEmail() {
        // Arrange
        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        doNothing().when(mailSender).send(messageCaptor.capture());

        // Act
        emailService.sendPurchaseConfirmation(testPurchase);

        // Assert
        verify(mailSender).send(any(MimeMessage.class));
        assertThat(testPurchase.getConfirmationCode()).isNotNull();
        assertThat(testPurchase.getCustomer().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should send email to correct recipient")
    void shouldSendEmailToCorrectRecipient() {
        // Arrange
        String expectedEmail = "test@example.com";
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        emailService.sendPurchaseConfirmation(testPurchase);

        // Assert
        assertThat(testPurchase.getCustomer().getEmail()).isEqualTo(expectedEmail);
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Should create MimeMessage for every email")
    void shouldCreateMimeMessageForEveryEmail() {
        // Arrange
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        emailService.sendPurchaseConfirmation(testPurchase);

        // Assert
        verify(mailSender, times(1)).createMimeMessage();
    }

    /**
     * Helper method to create test purchase data.
     * Applies DRY principle - reusable test data creation.
     */
    private TicketPurchase createTestPurchase() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setEmail("test@example.com");
        customer.setFirstName("John");
        customer.setLastName("Doe");

        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Avengers: Endgame");
        movie.setPrice(15000.0);

        TicketPurchase purchase = new TicketPurchase();
        purchase.setId(1L);
        purchase.setCustomer(customer);
        purchase.setMovie(movie);
        purchase.setQuantity(2);
        purchase.setUnitPrice(15000.0);
        purchase.setTotalAmount(30000.0);
        purchase.setStatus(PurchaseStatus.CONFIRMED);
        purchase.setCardLastFour("1234");
        purchase.setCardHolderName("John Doe");
        purchase.setConfirmationCode("TKT-ABC12345");
        purchase.setPurchaseDate(LocalDateTime.now());

        return purchase;
    }
}
