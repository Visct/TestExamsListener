package pl.kurs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
@RequiredArgsConstructor
public class EmailSendService {

    private final JavaMailSender javaMailSender;

    public void sendSimpleMessage(BytesMessage bytesMessage, File file) throws MessagingException, IOException, JMSException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom("testcheckmaster@gmail.com");
        mimeMessageHelper.setTo(bytesMessage.getStringProperty("email"));
        mimeMessageHelper.setSubject(bytesMessage.getStringProperty("emailSubject"));
        mimeMessageHelper.setText(bytesMessage.getStringProperty("emailText"));
        mimeMessageHelper.addAttachment(file.getName(), new ByteArrayResource(Files.readAllBytes(file.toPath())));

        javaMailSender.send(mimeMessage);
    }
}
