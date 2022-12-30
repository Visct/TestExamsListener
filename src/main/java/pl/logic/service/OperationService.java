package pl.logic.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import pl.logic.configuration.properties.AwsProperties;
import pl.logic.model.EmailJmsModel;

import javax.mail.MessagingException;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
@Slf4j

public class OperationService {
    private static final int BUFFER_SIZE = 4096;
    private final AmazonS3 amazonClient;
    private final AwsProperties awsProperties;
    private final EmailSendService emailSendService;
    private final TestCheckService testCheckService;
    private final ZipUnzipService zipUnzipService;

    public void getInboxEmailFromQueue(EmailJmsModel emailJmsModel) throws MessagingException, IOException {
        checkReceivedExam(emailJmsModel);
    }

    private String checkReceivedExam(EmailJmsModel emailJmsModel) throws IOException, MessagingException {
        checkTestsFromEmail(emailJmsModel);
        String examResult = testCheckService.examResult();
        emailSendService.sendSimpleMessage(examResult, "kontoall2011@gmail.com");
        cleanFolder("src/test/java");
        cleanFolder("src/main/java/pl/exam");

        return examResult;
    }

    private void checkTestsFromEmail(EmailJmsModel emailJmsModel) throws IOException {
        S3Object inboxGmailStudentFile = amazonClient.getObject(awsProperties.getBucketNameOfInboxExams(), emailJmsModel.getAwsFileName());
        S3Object testForExamFile = amazonClient.getObject(awsProperties.getBucketNameOfTests(), emailJmsModel.getAwsTestFileName());
        //File attachment = new File("771018b7-b7ae-4382-b3c1-aaa7a2829eb0.zip");        ////
        String emailStudenta = emailJmsModel.getEmail();                                       ////   <- SYMULACJA DOSTARCZENIA DANYCH
        Path operationFolder = Path.of("Operation");
        //UTWORZENIE FOLDERU OPERACYJNEGO
        Files.createDirectories(operationFolder);
        Files.copy(inboxGmailStudentFile.getObjectContent(), Path.of(operationFolder + File.separator + emailJmsModel.getAwsFileName()));
        Files.copy(testForExamFile.getObjectContent(),Path.of(operationFolder + File.separator + emailJmsModel.getAwsTestFileName()));
        inboxGmailStudentFile.close();
        testForExamFile.close();
        File attachment = new File (operationFolder + File.separator + emailJmsModel.getAwsFileName());
        File fileTest = new File (operationFolder + File.separator + emailJmsModel.getAwsTestFileName());

        //WYPAKOWANIE ZIPA OD STUDENTA DO FILDERU OPERACYJNEGO
        zipUnzipService.unzip(attachment.toPath().toString(), operationFolder.toString());
        zipUnzipService.unzip(fileTest.toPath().toString(), operationFolder.toString());

        //WRZUCENIE ZADAN DO PROJEKTU TESTEXAMLISTENER
        FileUtils.copyDirectory(Path.of("Operation" + File.separator + emailJmsModel.getAwsFileName()).toFile(), Path.of("src/test/java").toFile());
        //SKOPIOWANIE TESTOW DO PROJEKTU TESTEXAMLISTENER
        FileUtils.copyDirectory(Path.of("771018b7-b7ae-4382-b3c1-aaa7a2829eb0Operation/Exam/src/main/java/pl/kurs").toFile(), Path.of("src/main/java/pl/kurs").toFile());
        testForExamFile.close();

        runMvnTest();
    }

//    @Scheduled(cron = "")
//    public void processEveryNight() {
////Quartz
//    }

    private void runMvnTest() throws IOException {
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

    private void cleanFolder(String path) {
        // Create a File object for the folder
        File folder = new File(path);
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
