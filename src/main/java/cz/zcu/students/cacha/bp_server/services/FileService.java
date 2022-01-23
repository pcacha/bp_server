package cz.zcu.students.cacha.bp_server.services;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

@Service
public class FileService {
    Tika tika = new Tika();

    public String detectType(byte[] fileArr) {
        return tika.detect(fileArr);
    }
}
