package pl.kurs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailSendService {

    private final TestCheckService testCheckService;

    private final JavaMailSender javaMailSender;

    public String sendSimpleMessage() throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom("testcheckmaster@gmail.com");
        mimeMessageHelper.setTo("kontoall2011@gmail.com");
        mimeMessageHelper.setSubject("Wyniki testu");
        String result = testCheckService.examResult();
        mimeMessageHelper.setText(String.valueOf(result));

        javaMailSender.send(mimeMessage);
        return result;
    }
}
