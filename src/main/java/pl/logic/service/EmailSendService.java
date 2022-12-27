package pl.logic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailSendService {


    private final JavaMailSender javaMailSender;

    public void sendSimpleMessage(String examResult, String studentEmail) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom("testcheckmaster@gmail.com");
        mimeMessageHelper.setTo(studentEmail);
        mimeMessageHelper.setSubject("Wyniki testu");
        mimeMessageHelper.setText(examResult);

        javaMailSender.send(mimeMessage);

    }
}
