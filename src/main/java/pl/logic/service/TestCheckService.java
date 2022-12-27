
package pl.logic.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TestCheckService {


    private final FileProcessService fileProcessService;

    public String examResult() {

        Map<String, Integer> totalPoints = checkPoints("Tests run");
        Map<String, Integer> failurePoints = checkPoints("Failures");
        Map<String, Integer> pointsFromSpecificTask = totalPoints;
        failurePoints.forEach((key, value) -> pointsFromSpecificTask.put(key, pointsFromSpecificTask.get(key) - value));

        return examMessage(totalPoints, failurePoints, pointsFromSpecificTask);
    }

    public Map<String, Integer> checkPoints(String name) {
        return fileProcessService.checkPoints(name);
    }

    public String examMessage(Map<String, Integer> totalPoints, Map<String, Integer> failurePoints, Map<String, Integer> pointsFromSpecificTask) {
        int totalExamPoints = totalPoints.values().stream().reduce(0, Integer::sum);
        int receivedExamPoints = totalExamPoints - failurePoints.values().stream().reduce(0, Integer::sum);

        StringBuilder examResults = new StringBuilder();
        examResults.append("Maksymalna liczba punktów do zdobycia: ").append(totalExamPoints).append('\n');
        examResults.append("Liczba zdobytych punktów: ").append(receivedExamPoints).append('\n');
        examResults.append("Otrzymane punktu z poszczególnych zadań: ").append('\n');
        pointsFromSpecificTask.forEach((key, value) -> examResults.append(key).append(": ").append(value).append(" pkt").append('\n'));

        return examResults.toString();
    }
}

