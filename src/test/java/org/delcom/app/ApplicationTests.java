// src/test/java/org/delcom/app/AlarmApplicationTest.java
package org.delcom.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AlarmApplicationTest {

    @Test
    void contextLoads() {
        // Test dasar untuk memastikan Spring context dapat dimuat
        assertTrue(true, "Context should load successfully");
    }

    @Test
    void testMainMethod() {
        // Test untuk memastikan main method tidak throw exception
        assertDoesNotThrow(() -> {
            Application.main(new String[]{});
        });
    }
}