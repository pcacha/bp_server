package cz.zcu.students.cacha.bp_server.services;

import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

import static cz.zcu.students.cacha.bp_server.assets_store_config.WebConfiguration.INSTITUTIONS_IMAGES_FOLDER;

@Service
public class FileService {

    @Autowired
    Tika tika;

    public String detectType(byte[] fileArr) {
        return tika.detect(fileArr);
    }

    public String saveInstitutionImage(String encodedImage) throws IOException {
        String imageName = getRandomName();
        byte[] decodedBytes = Base64.getDecoder().decode(encodedImage);
        File target = new File(INSTITUTIONS_IMAGES_FOLDER + "/" + imageName);
        FileUtils.writeByteArrayToFile(target, decodedBytes);
        return imageName;
    }

    private String getRandomName() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
