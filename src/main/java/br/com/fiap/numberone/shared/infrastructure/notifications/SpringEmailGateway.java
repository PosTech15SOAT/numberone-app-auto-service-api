package br.com.fiap.numberone.shared.infrastructure.notifications;

import br.com.fiap.numberone.shared.application.gateways.EmailGateway;
import br.com.fiap.numberone.shared.infrastructure.config.MailProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class SpringEmailGateway implements EmailGateway {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    public SpringEmailGateway(JavaMailSender mailSender, MailProperties mailProperties) {
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
    }

    @Override
    public void send(String recipientEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        if (mailProperties.from() != null && !mailProperties.from().isBlank()) {
            message.setFrom(mailProperties.from());
        }
        message.setTo(recipientEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
