package cz.zcu.students.cacha.bp_server.services;

import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static cz.zcu.students.cacha.bp_server.assets_store_config.WebConfiguration.*;

@Service
public class FileService {

    @Autowired
    Tika tika;

    public String detectType(byte[] fileArr) {
        return tika.detect(fileArr);
    }

    public String saveInstitutionImage(String encodedImage) throws IOException {
        return saveImage(INSTITUTIONS_IMAGES_FOLDER, encodedImage);
    }

    public void deleteInstitutionImage(String image) {
        deleteImage(INSTITUTIONS_IMAGES_FOLDER, image);
    }

    public String saveExhibitImage(String encodedImage) throws IOException {
        return saveImage(EXHIBITS_IMAGES_FOLDER, encodedImage);
    }

    public void deleteExhibitImage(String image) {
        deleteImage(EXHIBITS_IMAGES_FOLDER, image);
    }

    public String saveInfoLabelImage(String encodedInfoLabel) throws IOException {
        return saveImage(INFO_LABELS_IMAGES_FOLDER, encodedInfoLabel);
    }

    public void deleteInfoLabelImage(String image) {
        deleteImage(INFO_LABELS_IMAGES_FOLDER, image);
    }

    private String saveImage(String directory, String encodedImage) throws IOException {
        String imageName = getRandomName();
        byte[] decodedBytes = Base64.getDecoder().decode(encodedImage);
        File target = new File(directory + "/" + imageName);
        FileUtils.writeByteArrayToFile(target, decodedBytes);
        return imageName;
    }

    private void deleteImage(String directory, String name) {
        try {
            Files.deleteIfExists(Paths.get(directory + "/" + name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getRandomName() {
        return (new Date().getTime()) + "_" +  UUID.randomUUID().toString().replace("-", "");
    }
}
