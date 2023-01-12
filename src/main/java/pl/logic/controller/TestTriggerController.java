package pl.logic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.logic.service.OperationService;

import javax.mail.MessagingException;
import java.io.IOException;

@RestController
@RequestMapping("api/v1/examcheck")
@RequiredArgsConstructor
@Validated

public class TestTriggerController {

    private final OperationService operationService;

//    @GetMapping
//    public ResponseEntity examCheck() throws IOException, MessagingException {
//        operationService.checkReceivedExam();
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }


}
