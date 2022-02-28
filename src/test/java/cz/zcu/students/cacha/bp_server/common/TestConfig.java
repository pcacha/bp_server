package cz.zcu.students.cacha.bp_server.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for tests
 */
@Configuration
public class TestConfig {

    /**
     * Gets the bean of test utils
     * @return test utils bean
     */
    @Bean
    public TestUtils testUtils() {
        return new TestUtils();
    }
}
