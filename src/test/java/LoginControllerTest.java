import com.timeline.App;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

/**
 * Unit tests for the LoginController.
 * @author Fredrik Ljungner fl222ai
 */
public class LoginControllerTest extends ApplicationTest {


    final String USERNAME_FIELD = "#usernameID";
    final String PASSWORD_FIELD = "#passwordID";
    final String SIGNIN_BUTTON = "#signInID";

    final String EXIT_BUTTON = "exitID";

    @BeforeEach
    void setUpClass() throws Exception {
        ApplicationTest.launch(App.class);
    }

    @BeforeEach
    void setUp () {
    }

    @Override
    public void start (Stage stage) throws Exception {
        stage.show();
    }

    @AfterEach
    void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }


    @Test
    void loginFail() {

        sleep(2000);

        clickOn(USERNAME_FIELD);
        write("Kalle");

        sleep(500);

        clickOn(PASSWORD_FIELD);
        write("HelloWorld");

        sleep(500);
        clickOn(SIGNIN_BUTTON);

        sleep(3000);
    }


    @Test
    void loginSuccessful() {

        sleep(2000);

        clickOn(USERNAME_FIELD);
        write("Kalle");

        sleep(500);

        clickOn(PASSWORD_FIELD);
        write("Godis123");

        sleep(500);

        clickOn(SIGNIN_BUTTON);

        sleep(3000);

    }



}
