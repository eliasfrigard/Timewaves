import com.timeline.EditTimelinePopupController;
import com.timeline.Timeline;
import org.junit.jupiter.api.Test;
import sql.TimelineMethods;

import java.util.ArrayList;

class EditTimelinePopupControllerTest {

    @Test
    void convertTimelineUnits() {
        // Add new timeline.
        Timeline tl = new Timeline("Elias Test Timeline", 0, 100, "Minutes");
        // tl.addEvent("Test", 50, "AAA", "BBB", "src/main/resources/images/eventPictures/298-0.png");
        TimelineMethods.insertTimeline(tl);

       ArrayList<Timeline> list = TimelineMethods.getTimelines();

        for (Timeline tl2 : list) {
            if (tl2.getName().equalsIgnoreCase(tl.getName())) {
                tl = tl2;
            }
        }

        EditTimelinePopupController.convertTimelineUnits(tl, "Seconds");
    }
}