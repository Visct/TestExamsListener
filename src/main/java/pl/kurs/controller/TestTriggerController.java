package pl.kurs.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kurs.service.EmailSendService;
import pl.kurs.service.TestCheckService;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/examcheck")
@RequiredArgsConstructor
@Validated

public class TestTriggerController {

    private final TestCheckService testCheckService;
    private final EmailSendService emailSendService;

    @GetMapping
    public ResponseEntity examCheck() throws IOException, InterruptedException {
        testCheckService.checkTestsFromEmail();
        testCheckService.runMvnTest();
        double result = testCheckService.examResult();
        testCheckService.cleanFolder();
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse(result));
    }


}
