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
 * Test for CreateTimelinePopupController.
 * @author Susanna Persson sp222xw
 */

class CreateTimelinePopupControllerTest extends ApplicationTest {

    @BeforeEach
    void setUp () throws Exception {
        ApplicationTest.launch(App.class);

        // To test Create Timeline functions, the user must be logged in. Preparing fields for login
        String USERNAME_FIELD = "#usernameID";
        String PASSWORD_FIELD = "#passwordID";
        String SIGNIN_BUTTON = "#signInID";

        // Write username
        clickOn(USERNAME_FIELD);
        write("Susanna");
        // Write password
        clickOn(PASSWORD_FIELD);
        write("Kaffe00");
        // Click log in
        clickOn(SIGNIN_BUTTON);
        WaitForAsyncUtils.waitForFxEvents();

        // Find and click on create button
        String CREATE_TIMELINE = "#createButton";
        clickOn(CREATE_TIMELINE);
        //sleep(3000);
    }

    @AfterEach
    void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Override
    public void start (Stage stage) throws Exception {
        stage.show();
    }

    @Test
    void closePopUp() {
        String CANCEL_BUTTON = "#cancel";
        clickOn(CANCEL_BUTTON);
    }

    @Test
    void onSubmitNoNameErrorMsg() {
        String NAME_ERROR_LABEL = "#nameErrorLabel";
        String SUBMIT_BUTTON = "#submit";
        clickOn(SUBMIT_BUTTON);
        verifyThat(NAME_ERROR_LABEL, NodeMatchers.isVisible());
    }

    @Test
    void onSubmitNoUnitErrorMsg() {
        String NAME_INPUT_FIELD = "#timelineName";
        String UNIT_ERROR_LABEL = "#unitErrorLabel";
        String SUBMIT_BUTTON = "#submit";
        clickOn(NAME_INPUT_FIELD);
        write("TestFXRobotSaysHello");
        clickOn(SUBMIT_BUTTON);
        verifyThat(UNIT_ERROR_LABEL, NodeMatchers.isVisible());
    }

    @Test
    void addKeyword() {
        String KEYWORD_INPUT_FIELD = "#inputKeywordsField";

        clickOn(KEYWORD_INPUT_FIELD);
        write("testfx");
    }
}