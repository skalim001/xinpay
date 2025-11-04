package com.xinpay.backend.service;

import com.resend.Resend;
import com.resend.services.emails.model.SendEmailRequest;
import com.resend.services.emails.model.SendEmailResponse;
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
            Resend resend = new Resend(resendApiKey);

            SendEmailRequest request = SendEmailRequest.builder()
                    .from(fromEmail)
                    .to(toEmail)
                    .subject(subject)
                    .html("<pre style='font-family:inherit;'>" + body + "</pre>")
                    .build();

            SendEmailResponse response = resend.emails().send(request);
            System.out.println("✅ Email sent successfully: " + response.getId());
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }
}
