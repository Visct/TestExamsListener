
package pl.kurs.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import pl.kurs.configuration.properties.AwsProperties;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
public class TestCheckService {

    private static final int BUFFER_SIZE = 4096;
    private final AmazonS3 amazonClient;
    private final AwsProperties awsProperties;


    public void checkTestsFromEmail() throws IOException {
        File attachment = new File("771018b7-b7ae-4382-b3c1-aaa7a2829eb0.zip");        ////
        String emailStudenta = "kontoall2011@gmail.com";                                       ////   <- SYMULACJA DOSTARCZENIA DANYCH
        Path operationFolder = Path.of("771018b7-b7ae-4382-b3c1-aaa7a2829eb0Operation");  ////

        //UTWORZENIE FOLDERU OPERACYJNEGO
        Files.createDirectories(operationFolder);
        //WYPAKOWANIE ZIPA OD STUDENTA DO FILDERU OPERACYJNEGO
        unzip(attachment.toPath().toString(), operationFolder.toString());
        //POBRANIE ZIPA Z TESTAMI PRZYGOTOWANYMI POD DANY TEST
        S3Object s3object = amazonClient.getObject(awsProperties.getBucketNameOfTests(), "771018b7-b7ae-4382-b3c1-aaa7a2829eb0Tests.zip");
        //ZAPISANIE ZIPA Z TESTAMI DO FOLDERU OPERACYJNEGO
        Files.copy(s3object.getObjectContent(), Path.of(operationFolder + File.separator + "test.zip"));
        //WYPAKOWANIE ZIPA Z TESTAMI DO FOLDERU OPERACYJNEGO
        unzip(operationFolder + File.separator + "test.zip", operationFolder.toString());
        //WRZUCENIE ZADAN DO PROJEKTU TESTEXAMLISTENER
        FileUtils.copyDirectory(Path.of("771018b7-b7ae-4382-b3c1-aaa7a2829eb0Operation/Tests").toFile(), Path.of("src/test/java").toFile());
        //SKOPIOWANIE TESTOW DO PROJEKTU TESTEXAMLISTENER
        FileUtils.copyDirectory(Path.of("771018b7-b7ae-4382-b3c1-aaa7a2829eb0Operation/Exam/src/main/java/pl/kurs").toFile(), Path.of("src/main/java/pl/kurs").toFile());
        s3object.close();


    }

    public void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdirs();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }

    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    public String examResult() {

        Map<String, Integer> totalPoints = checkPoints("Tests run");
        Map<String, Integer> failurePoints = checkPoints("Failures");
        Map<String, Integer> pointsFromSpecificTask = totalPoints;

        failurePoints.forEach((key, value) -> pointsFromSpecificTask.put(key, pointsFromSpecificTask.get(key) - value));
        return pointsFromSpecificTask.toString();
    }

    public Map<String, Integer> checkPoints(String name) {
        Map<String, Integer> points = new LinkedHashMap<>();
        AtomicReference<String> testName = new AtomicReference<>();
        try (Stream<Path> paths = Files.walk(Paths.get("target/surefire-reports"))) {
            paths
                    .filter(file -> file.toString().endsWith("txt"))
                    .forEach(f -> {
                        BufferedReader br = null;
                        try {
                            br = new BufferedReader(new FileReader(f.toFile()));
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                        String line;
                        while (true) {
                            try {
                                if ((line = br.readLine()) == null) break;
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            String[] words = line.split(",");
                            for (String word : words) {
                                if (word.contains("Test set")) {
                                    testName.set(word.substring(word.lastIndexOf(" ") + 1));
                                }
                                if (word.contains(name)) {
                                    points.put(String.valueOf(testName), Integer.parseInt(word.substring(word.lastIndexOf(" ") + 1)));
                                }
                            }
                        }

                        try {
                            br.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return points;
    }


    public void runMvnTest() throws IOException {
        Process p = Runtime.getRuntime().exec("cmd /c mvn test");
        String s;
        System.out.println(p.getOutputStream().toString());
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));
        StringBuilder sb = new StringBuilder();
        while ((s = stdInput.readLine()) != null) {
            sb.append(s);
        }
        System.out.println(sb);
    }

    public void cleanFolder() {
        // Set the folder path
        String folderPath = "src/test/java";
        // Create a File object for the folder
        File folder = new File(folderPath);
        // Check if the folder exists
        if (folder.exists()) {
            // Get the list of files in the folder
            File[] files = folder.listFiles();
            // Loop through the files and delete them
            for (File file : files) {
                file.delete();
            }
        }
    }
}

