package pl.logic.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import pl.logic.model.EmailJmsModel;
import pl.logic.service.OperationService;

@Service
@RequiredArgsConstructor
@Slf4j
public class InboxEmailEventListener {
    private final ObjectMapper objectMapper;
    private final OperationService operationService;

    @JmsListener(destination = "inbox-email-queue")
    @SneakyThrows
    public void subscribeInboxEmailEvent(String emailEvent){
        EmailJmsModel emailJmsModel = objectMapper.readValue(emailEvent, EmailJmsModel.class);
        log.info("Consuming event {}", emailEvent);
        operationService.checkReceivedExam(emailJmsModel);
    }
}
