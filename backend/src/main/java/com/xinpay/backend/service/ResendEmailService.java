package com.xinpay.backend.service;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ResendEmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Value("${spring.mail.from}")
    private String fromEmail;

    public void sendEmail(String toEmail, String subject, String body) {
        try {
            // Initialize Resend client
            Resend resend = new Resend(resendApiKey);

            // Build email request
            CreateEmailOptions request = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(toEmail)
                    .subject(subject)
                    .html("<div style='font-family: Arial, sans-serif;'>" + body + "</div>")
                    .build();

            // Send email
            CreateEmailResponse response = resend.emails().send(request);
            System.out.println("✅ Email sent successfully. ID: " + response.getId());

        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }
}
