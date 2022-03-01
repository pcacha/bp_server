package cz.zcu.students.cacha.bp_server.integration_tests;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Institution class for testing purposes
 */
@Data
@NoArgsConstructor
public class MockInstitution {
    /**
     * institution name
     */
    private String name;
    /**
     * address
     */
    private String address;
    /**
     * string with latitude
     */
    private String latitudeString;
    /**
     * string with longitude
     */
    private String longitudeString;

    /**
     * Creates new instance with given params
     * @param name name
     * @param address address
     * @param latitudeString latitude
     * @param longitudeString longitude
     */
    public MockInstitution(String name, String address, String latitudeString, String longitudeString) {
        this.name = name;
        this.address = address;
        this.latitudeString = latitudeString;
        this.longitudeString = longitudeString;
    }
}
