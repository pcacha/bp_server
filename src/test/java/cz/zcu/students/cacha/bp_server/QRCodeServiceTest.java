package cz.zcu.students.cacha.bp_server;

import cz.zcu.students.cacha.bp_server.services.QRCodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Set of tests to check the quality of QRCodeService class
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class QRCodeServiceTest {

    @Autowired
    private QRCodeService qrCodeService;

    /**
     * Tests generating of qr code
     */
    @Test
    public void testGenerateQRCodeImage() {
        // tests no exception is thrown
        assertDoesNotThrow(() -> {
            BufferedImage bufferedImage = qrCodeService.generateQRCodeImage("test-text");
            // check that buffered image was generated
            assertNotNull(bufferedImage);
        });

    }
}
