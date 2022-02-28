package cz.zcu.students.cacha.bp_server.responses;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class represents response that is returned as information if request was processed successfully
 */
@Data
@NoArgsConstructor
public class GenericResponse {
    /**
     * response message
     */
    private String message;

    /**
     * Creates new instance with given message
     * @param message response message
     */
    public GenericResponse(String message) {
        this.message = message;
    }
}

