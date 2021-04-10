import com.timeline.App;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.util.WaitForAsyncUtils;

import static org.testfx.api.FxAssert.verifyThat;

/**
 * Unit tests for the RegisterController class using TestFX.
 * @author Joel Salo js225fg
 */
public class RegisterControllerTest extends ApplicationTest {

    // temporary constant until pathing gets sorted
    final String REGISTER_VIEW = "#registerBtn";

    final String USERNAME_FIELD = "#username";
    final String PASSWORD_FIELD = "#password";
    final String CONFIRM_PASSWORD_FIELD = "#confirmPassword";

    final String PASSWORD_LABEL = "#passwordLabel";
    final String CONFIRM_PASSWORD_LABEL = "#confirmPasswordLabel";

    final String EMAIL_FIELD = "#email";
    final String CONFIRM_EMAIL_FIELD = "#confirmEmail";

    final String EMAIL_LABEL = "#emailLabel";
    final String CONFIRM_EMAIL_LABEL = "#confirmEmailLabel";

    final String REGISTER_BUTTON = "#register";

    @BeforeEach
    void setUpClass() throws Exception {
        ApplicationTest.launch(App.class);

        // temporary fix until pathing gets sorted
        clickOn(REGISTER_VIEW);
        WaitForAsyncUtils.waitForFxEvents();
    }

    @BeforeEach
    void setUp() {
    }

    @Override
    public void start (Stage stage) throws Exception {
        stage.show();
    }

    @AfterEach
    void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Test
    void passwordsShouldMatch() {
        clickOn(PASSWORD_FIELD);
        write("Test123");

        clickOn(CONFIRM_PASSWORD_FIELD);
        write("Test123");

        verifyThat(CONFIRM_PASSWORD_LABEL, NodeMatchers.isInvisible());
    }

    @Test
    void passwordsShouldNotMatch() {
        clickOn(PASSWORD_FIELD);
        write("Test123");

        clickOn(CONFIRM_PASSWORD_FIELD);
        write("Test1234");

        verifyThat(CONFIRM_PASSWORD_LABEL, NodeMatchers.isVisible());
    }

    @Test
    void shouldFulfillPasswordCriteria() {
        String[] input = { "Test123", "ThIsIsA7est", "testingThis1", "12345asdASD", "FINALTeST1" };

        for (String pass : input) {
            clickOn(PASSWORD_FIELD);
            write(pass);

            WaitForAsyncUtils.waitForFxEvents();
            verifyThat(PASSWORD_LABEL, NodeMatchers.isInvisible());
            doubleClickOn(PASSWORD_FIELD).eraseText(pass.length());
        }

    }

    @Test
    void shouldNotFulfillPasswordCriteria() {
        String[] input = { "test", "test1", "testTEST", "test!1", "f1n4lt3s7!" };

        for (String pass : input) {
            clickOn(PASSWORD_FIELD);
            write(pass);

            WaitForAsyncUtils.waitForFxEvents();
            verifyThat(PASSWORD_LABEL, NodeMatchers.isVisible());
            doubleClickOn(PASSWORD_FIELD).eraseText(pass.length());
        }
    }

    @Test
    void shouldFulfillEmailCriteria() {
        String input = "test@test.com";

        clickOn(EMAIL_FIELD);
        write(input);

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(EMAIL_LABEL, NodeMatchers.isInvisible());
        doubleClickOn(EMAIL_FIELD).eraseText(input.length());
    }

    @Test
    void shouldNotFulfillEmailCriteria() {
        String input = "test@";

        clickOn(EMAIL_FIELD);
        write(input);

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(EMAIL_LABEL, NodeMatchers.isVisible());
        doubleClickOn(EMAIL_FIELD).eraseText(input.length());
    }

    @Test
    void emailShouldMatch() {
        clickOn(EMAIL_FIELD);
        write("test@test.com");

        clickOn(CONFIRM_EMAIL_FIELD);
        write("test@test.com");

        verifyThat(CONFIRM_EMAIL_LABEL, NodeMatchers.isInvisible());
    }

    @Test
    void emailShouldNotMatch() {
        clickOn(EMAIL_FIELD);
        write("test@test.com");

        clickOn(CONFIRM_EMAIL_FIELD);
        write("test@test.co");

        verifyThat(CONFIRM_EMAIL_LABEL, NodeMatchers.isVisible());
    }
}

