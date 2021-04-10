import org.junit.jupiter.api.Test;
import sql.TimelineMethods;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Anton Munter & Stefan Jägstrand
 */
public class TimelineMethodsTest {

    /** Anton Munter & Stefan Jägstrand #8 */
    @Test
    public void testGetTimeLinesMethodDoesNotThrowException() {
        // Test that connection to database works without problems.
        assertDoesNotThrow(() -> TimelineMethods.getTimelines());
    }
}
