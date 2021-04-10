import com.timeline.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import sql.UserMethods;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the UserMethods class
 * @author Joel Salo js225fg
 */
public class UserMethodsTest extends ApplicationTest {

    User sut;

    @BeforeEach
    void setUp() throws Exception {
        // set up the test-user (sut) - isAdmin defaults to false
        sut = new User("TESTUnitTEST", "Testing123");
        UserMethods.addUser(sut);
    }

    @AfterEach
    void tearDown() throws Exception {
        // delete the user to avoid conflicts when running new tests
        UserMethods.deleteUser(sut);
        sut = null;
    }

    @Test
    void shouldReturnAdmin() {
        UserMethods.toggleAdminStatus(sut);

        assertTrue(sut.isAdmin(), "Should evaluate as 'true' - the user is flagged as administrator");
    }

    @Test
    void shouldReturnNotAdmin() {
        assertFalse(sut.isAdmin(), "Should evaluate as 'false' - the user is not flagged as administrator");
    }

    @Test
    void shouldReturnNotAdminToggleFunction() {
        UserMethods.toggleAdminStatus(sut);
        UserMethods.toggleAdminStatus(sut);

        assertFalse(sut.isAdmin(), "Should evaluate as 'false' - toggle admin status on and off");
    }

    @Test
    void authenticateUserSuccess() {
        boolean expected = true;
        boolean actual = UserMethods.authenticateUser(sut);

        assertEquals(expected, actual, "Should evaluate as 'true' - user is authenticated");
    }

    @Test
    void authenticateUserFailure() {
        sut.setPassword("Testing1234");
        boolean expected = false;
        boolean actual = UserMethods.authenticateUser(sut);

        assertEquals(expected, actual, "Should evaluate as 'false' - user is not authenticated, wrong password");
        sut.setPassword("Testing123");
    }

    @Test
    void shouldReturnInRecovery() {
        UserMethods.setInRecovery(sut, true);

        assertTrue(sut.isInRecovery(), "Should evaluate as 'true' - the user is flagged as in recovery mode");
    }

    @Test
    void shouldReturnNotInRecovery() {
        assertFalse(sut.isInRecovery(), "Should evaluate as 'false' - the user is not flagged as in recovery mode");
    }
}
