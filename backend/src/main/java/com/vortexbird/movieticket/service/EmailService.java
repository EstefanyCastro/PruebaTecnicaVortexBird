package com.vortexbird.movieticket.service;

import com.vortexbird.movieticket.model.TicketPurchase;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * Email service implementation.
 * 
 * Applies SOLID principles:
 * - Single Responsibility: Only handles email sending
 * - Dependency Inversion: Depends on JavaMailSender abstraction
 * 
 * Uses @Async for non-blocking email sending (KISS principle).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService implements IEmailService {

    private final JavaMailSender mailSender;
    
    private static final String FROM_EMAIL = "noreply@movieticket.com";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Sends purchase confirmation email asynchronously.
     * 
     * @param purchase The ticket purchase with all details
     */
    @Async
    @Override
    public void sendPurchaseConfirmation(TicketPurchase purchase) {
        try {
            log.info("Sending purchase confirmation email to: {}", purchase.getCustomer().getEmail());
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(FROM_EMAIL);
            helper.setTo(purchase.getCustomer().getEmail());
            helper.setSubject("Confirmación de Compra - Movie Ticket");
            helper.setText(buildEmailContent(purchase), true);
            
            mailSender.send(message);
            log.info("Purchase confirmation email sent successfully to: {}", purchase.getCustomer().getEmail());
            
        } catch (MessagingException e) {
            log.error("Failed to send purchase confirmation email to: {}. Error: {}", 
                     purchase.getCustomer().getEmail(), e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error sending purchase confirmation email to: {}. Error: {}", 
                     purchase.getCustomer().getEmail(), e.getMessage(), e);
        }
    }

    /**
     * Builds HTML email content.
     * 
     * Applies DRY principle by centralizing email template generation.
     */
    private String buildEmailContent(TicketPurchase purchase) {
        String customerName = purchase.getCustomer().getFirstName() + " " + purchase.getCustomer().getLastName();
        String purchaseDate = purchase.getPurchaseDate().format(DATE_FORMATTER);
        
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #dc3545; color: white; padding: 20px; text-align: center; }
                    .content { background-color: #f8f9fa; padding: 20px; margin: 20px 0; }
                    .ticket-info { background-color: white; padding: 15px; margin: 10px 0; border-left: 4px solid #dc3545; }
                    .confirmation-code { font-size: 24px; font-weight: bold; color: #dc3545; text-align: center; margin: 20px 0; }
                    .footer { text-align: center; color: #6c757d; font-size: 12px; margin-top: 20px; }
                    table { width: 100%%; border-collapse: collapse; }
                    td { padding: 8px; }
                    .label { font-weight: bold; color: #495057; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎬 Movie Ticket</h1>
                        <p>Confirmación de Compra</p>
                    </div>
                    
                    <div class="content">
                        <h2>¡Hola %s!</h2>
                        <p>Tu compra ha sido procesada exitosamente. A continuación encontrarás los detalles:</p>
                        
                        <div class="confirmation-code">
                            Código de Confirmación: %s
                        </div>
                        
                        <div class="ticket-info">
                            <h3>Detalles de la Compra</h3>
                            <table>
                                <tr>
                                    <td class="label">Película:</td>
                                    <td>%s</td>
                                </tr>
                                <tr>
                                    <td class="label">Cantidad de Boletos:</td>
                                    <td>%d</td>
                                </tr>
                                <tr>
                                    <td class="label">Precio por Boleto:</td>
                                    <td>$%,.2f COP</td>
                                </tr>
                                <tr>
                                    <td class="label">Total Pagado:</td>
                                    <td><strong>$%,.2f COP</strong></td>
                                </tr>
                                <tr>
                                    <td class="label">Fecha de Compra:</td>
                                    <td>%s</td>
                                </tr>
                                <tr>
                                    <td class="label">Estado:</td>
                                    <td><strong>%s</strong></td>
                                </tr>
                            </table>
                        </div>
                        
                        <p><strong>Información de Pago:</strong></p>
                        <p>Tarjeta: **** **** **** %s</p>
                        <p>Titular: %s</p>
                        
                        <p style="margin-top: 20px;">Recuerda presentar este correo o tu código de confirmación en la taquilla del cine.</p>
                    </div>
                    
                    <div class="footer">
                        <p>Este es un correo automático, por favor no responder.</p>
                        <p>&copy; 2026 Movie Ticket. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                customerName,
                purchase.getConfirmationCode(),
                purchase.getMovie().getTitle(),
                purchase.getQuantity(),
                purchase.getUnitPrice(),
                purchase.getTotalAmount(),
                purchaseDate,
                purchase.getStatus().name(),
                purchase.getCardLastFour(),
                purchase.getCardHolderName()
            );
    }
}
