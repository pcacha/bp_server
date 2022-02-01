package cz.zcu.students.cacha.bp_server.assets_store_config;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.*;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    public static final String INSTITUTIONS_IMAGES_FOLDER = "institutions_images";
    public static final String EXHIBITS_IMAGES_FOLDER = "exhibits_images";
    public static final String INFO_LABELS_IMAGES_FOLDER = "info_labels_images";

    public static final String DEFAULT_INSTITUTION_IMAGE = "default_institution.png";
    public static final String DEFAULT_EXHIBIT_IMAGE = "default_exhibit.png";
    public static final String DEFAULT_IMAGE = "default.png";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/" + INSTITUTIONS_IMAGES_FOLDER + "/**")
                .addResourceLocations("file:" + INSTITUTIONS_IMAGES_FOLDER + "/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS));

        registry.addResourceHandler("/" + EXHIBITS_IMAGES_FOLDER + "/**")
                .addResourceLocations("file:" + EXHIBITS_IMAGES_FOLDER + "/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS));

        registry.addResourceHandler("/" + INFO_LABELS_IMAGES_FOLDER + "/**")
                .addResourceLocations("file:" + INFO_LABELS_IMAGES_FOLDER + "/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS));
    }

    @Bean
    CommandLineRunner initImagesFolders() {
        return (args) -> {
            createNonExistingFolder(INSTITUTIONS_IMAGES_FOLDER);
            createNonExistingFolder(EXHIBITS_IMAGES_FOLDER);
            createNonExistingFolder(INFO_LABELS_IMAGES_FOLDER);

            addNonExistingDefaultImage(INSTITUTIONS_IMAGES_FOLDER, DEFAULT_INSTITUTION_IMAGE);
            addNonExistingDefaultImage(EXHIBITS_IMAGES_FOLDER, DEFAULT_EXHIBIT_IMAGE);
        };
    }

    private void addNonExistingDefaultImage(String dir, String defaultName) throws IOException {
        String path = dir + "/" + defaultName;
        File image = new File(path);
        boolean imageExists = image.exists() && image.isFile();

        if(!imageExists) {
            try(InputStream is =  getClass().getClassLoader().getResourceAsStream("static/" + DEFAULT_IMAGE)) {
                try(OutputStream os = new FileOutputStream(image)){
                    IOUtils.copy(is, os);
                }
            }
        }
    }

    private void createNonExistingFolder(String path) {
        File folder = new File(path);
        boolean folderExists = folder.exists() && folder.isDirectory();
        if(!folderExists) {
            folder.mkdir();
        }
    }
}