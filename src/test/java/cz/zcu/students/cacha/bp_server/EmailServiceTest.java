package cz.zcu.students.cacha.bp_server;

import cz.zcu.students.cacha.bp_server.services.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Set of tests to check the quality of EmailService class
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Test
    public void testSendSimpleMailMessage() {
        // check sending mail does not throw an exception
        assertDoesNotThrow(() -> emailService.sendSimpleMessage("pavelcacha@email.cz", "test", "test text"));
    }
}
