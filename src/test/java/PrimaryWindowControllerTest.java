import com.timeline.App;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

/**
 * @author Elias Holmér
 */
class PrimaryWindowControllerTest extends ApplicationTest {

    @BeforeEach
    void setUp() throws Exception {
        ApplicationTest.launch(App.class);
        sleep(1000);
        clickOn("#continueBtn");
        sleep(1000);
    }

    @Test
    void fillMenu() {
        clickOn("#menuButton");
        sleep(1000);
        // clickOn("#editTimelineButton");
        sleep(500);
        clickOn("#menuButton");
        sleep(2000);
    }
}
