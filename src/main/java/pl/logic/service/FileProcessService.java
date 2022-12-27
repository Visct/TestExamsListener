package pl.logic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileProcessService {

    public Map<String, Integer> checkPoints(String name) {
        Map<String, Integer> points = new LinkedHashMap<>();
        try (Stream<Path> paths = Files.walk(Paths.get("target/surefire-reports"))) {
            paths
                    .filter(file -> file.toString().endsWith("txt"))
                    .forEach(f -> processTestFile(f.toFile(), points, name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return points;
    }

    private void processTestFile(File f, Map<String, Integer> points, String name) {
        AtomicReference<String> testName = new AtomicReference<>();
        try (Stream<String> lines = new BufferedReader(new FileReader(f)).lines()) {
            lines.forEach(line ->
                    {
                        if (line.contains("Test set")) {
                            testName.set(line.substring(line.lastIndexOf(" ") + 1, line.length() - 4));
                        } else {
                            processLine(line, points, name, testName.toString());
                        }
                    }
            );
        } catch (Exception exc) {
            log.error("Error during processing file", exc);
        }
    }

    private void processLine(String line, Map<String, Integer> points, String name, String testName) {
        if (testName != null) {
            String[] words = line.split(",");
            initPoints(points, name, testName, words);
        }
    }

    private void initPoints(Map<String, Integer> points, String name, String testName, String[] words) {
        for (String word : words) {
            if (word.contains(name)) {
                points.put(String.valueOf(testName), Integer.parseInt(word.substring(word.lastIndexOf(" ") + 1)));
            }
        }
    }
}
