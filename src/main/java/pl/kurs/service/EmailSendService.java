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

    private final TestCheckService testCheckService;

    private final JavaMailSender javaMailSender;

    public double sendSimpleMessage() throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom("testcheckmaster@gmail.com");
        mimeMessageHelper.setTo("kontoall2011@gmail.com");
        mimeMessageHelper.setSubject("Wyniki testu");
        double result = testCheckService.examResult();
        mimeMessageHelper.setText(String.valueOf(result));

        javaMailSender.send(mimeMessage);
        return result;
    }
}
