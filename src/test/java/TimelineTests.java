import com.timeline.Timeline;
import com.timeline.TimelineEvent;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for Timeline class.
 * @author Susanna Persson sp222xw
 */

public class TimelineTests {

    @Test
    public void testAllGettersSetters () {
        // Simple test to verify that all getters & setters works correctly
        int id = 10;
        String name = "Timeline_123";
        long startUnit = 10;
        long endUnit = 233;
        String unitName = "minutes of my life";
        String[] keywords = new String[] {"potato", "unicorn", "sunburn"};

        // Create timeline & verify getters
        Timeline testTimeline = new Timeline(id, name, startUnit, endUnit, unitName, keywords);

        assertEquals(id, testTimeline.getId());
        assertEquals(name, testTimeline.getName());
        assertEquals(startUnit, testTimeline.getStartUnit());
        assertEquals(endUnit, testTimeline.getEndUnit());
        assertEquals(unitName, testTimeline.getUnitName());
        assertEquals(keywords.length, testTimeline.getKeywords().length);
    }

    @Test
    public void testAddEventObject() {
        String eventName = "Test Event";
        long placeOnTimeline = 123;
        String shortDesc = "Lorem ipsum";
        String longDesc = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras sodales feugiat lorem eget pretium. Curabitur elit enim, dignissim nec bibendum sit amet, imperdiet ac purus. Maecenas viverra quam vitae nisi iaculis ultricies. Cras convallis eleifend lorem ac placerat. Cras placerat semper nisi, id gravida dolor semper id. Mauris eleifend massa non urna eleifend molestie vel vitae lectus. Quisque maximus non metus dictum venenatis. Maecenas egestas, purus bibendum pharetra placerat, erat ante euismod dui, vitae porttitor felis sem quis est. Etiam ut mollis nisi. Donec interdum tempus tortor sit amet vehicula. ";

        Timeline testTimeline = new Timeline();
        TimelineEvent testEvent = new TimelineEvent(testTimeline, eventName, placeOnTimeline, shortDesc, longDesc, "");
        testTimeline.addEvent(testEvent);

        assertEquals(testEvent, testTimeline.getEvents()[0]);
    }

    @Test
    public void testAddNewEvent() {
        String eventName = "Test Event";
        long placeOnTimeline = 123;
        String shortDesc = "Lorem ipsum";
        String longDesc = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras sodales feugiat lorem eget pretium. Curabitur elit enim, dignissim nec bibendum sit amet, imperdiet ac purus. Maecenas viverra quam vitae nisi iaculis ultricies. Cras convallis eleifend lorem ac placerat. Cras placerat semper nisi, id gravida dolor semper id. Mauris eleifend massa non urna eleifend molestie vel vitae lectus. Quisque maximus non metus dictum venenatis. Maecenas egestas, purus bibendum pharetra placerat, erat ante euismod dui, vitae porttitor felis sem quis est. Etiam ut mollis nisi. Donec interdum tempus tortor sit amet vehicula. ";
        String imagePath = "path/to/file.jpg";

        int expected = 1;

        Timeline testTimeline = new Timeline();
        testTimeline.addEvent(eventName, placeOnTimeline, shortDesc, longDesc, imagePath);

        assertEquals(expected, testTimeline.getNoOfEvents());
    }

    @Test
    public void testAddNullEventShouldThrowException () {
        // Add null event to timeline
        Timeline testTimeline = new Timeline();
        TimelineEvent faultyEvent = null; // Uh oh!
        // addEvent should detect that it's null and throw exception.
        assertThrows(IllegalArgumentException.class, () -> testTimeline.addEvent(faultyEvent));
    }

    @Test
    public void testRemoveEvent() {
        String eventName = "Test Event";
        long placeOnTimeline = 123;
        String shortDesc = "Lorem ipsum";
        String longDesc = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras sodales feugiat lorem eget pretium. Curabitur elit enim, dignissim nec bibendum sit amet, imperdiet ac purus. Maecenas viverra quam vitae nisi iaculis ultricies. Cras convallis eleifend lorem ac placerat. Cras placerat semper nisi, id gravida dolor semper id. Mauris eleifend massa non urna eleifend molestie vel vitae lectus. Quisque maximus non metus dictum venenatis. Maecenas egestas, purus bibendum pharetra placerat, erat ante euismod dui, vitae porttitor felis sem quis est. Etiam ut mollis nisi. Donec interdum tempus tortor sit amet vehicula. ";

        int expected = 0;

        Timeline testTimeline = new Timeline();
        TimelineEvent testEvent = new TimelineEvent(testTimeline, eventName, placeOnTimeline, shortDesc, longDesc, "");
        testTimeline.addEvent(testEvent);
        testTimeline.removeEvent(testEvent);

        assertEquals(expected, testTimeline.getNoOfEvents());
    }

    @Test
    public void testContainsEvent() {
        String eventName = "Test Event";
        long placeOnTimeline = 123;
        String shortDesc = "Lorem ipsum";
        String longDesc = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras sodales feugiat lorem eget pretium. Curabitur elit enim, dignissim nec bibendum sit amet, imperdiet ac purus. Maecenas viverra quam vitae nisi iaculis ultricies. Cras convallis eleifend lorem ac placerat. Cras placerat semper nisi, id gravida dolor semper id. Mauris eleifend massa non urna eleifend molestie vel vitae lectus. Quisque maximus non metus dictum venenatis. Maecenas egestas, purus bibendum pharetra placerat, erat ante euismod dui, vitae porttitor felis sem quis est. Etiam ut mollis nisi. Donec interdum tempus tortor sit amet vehicula. ";

        boolean expected = true;

        Timeline testTimeline = new Timeline();
        TimelineEvent testEvent = new TimelineEvent(testTimeline, eventName, placeOnTimeline, shortDesc, longDesc, "");
        testTimeline.addEvent(testEvent);

        assertEquals(expected, testTimeline.containsEvent(testEvent));
    }

    @Test
    void getRange() {
        Timeline testTimeline = new Timeline();
        long expected = 5;

        testTimeline.setStartUnit(5);
        testTimeline.setEndUnit(10);

        assertEquals(expected, testTimeline.getRange());
    }

    // The tests below currently fail. / Susanna
    // Hopefully fixed now. / Stefan

    @Test
    void getSmallestRange() {
        // Create timeline and populate it with events
        TimelineEvent testEvent;
        Timeline testTimeline = new Timeline();
        testTimeline.setStartUnit(0);
        testTimeline.setEndUnit(200);

        long[] testPlaces = new long[] {1, 5, 42, 44, 100};

        for (int i = 0; i < 5; i++) {
            testEvent = new TimelineEvent();
            testEvent.setPlaceOnTimeline(testPlaces[i]);
            testTimeline.addEvent(testEvent);
        }

        // Smallest range should be between 42 & 44 (== 2)
        long expected = 2;

        assertEquals(expected, testTimeline.getSmallestRange());
    }

    @Test
    void getLargestRange() {
        // Create timeline and populate it with events
        TimelineEvent testEvent;
        Timeline testTimeline = new Timeline();
        testTimeline.setStartUnit(0);
        testTimeline.setEndUnit(200);

        long[] testPlaces = new long[] {1, 5, 42, 44, 100};

        for (int i = 0; i < 5; i++) {
            testEvent = new TimelineEvent();
            testEvent.setPlaceOnTimeline(testPlaces[i]);
            testTimeline.addEvent(testEvent);
        }

        // Largest range should be between 44 & 100 (==56)
        long expected = 56;

        assertEquals(expected, testTimeline.getLargestRange());
    }

    @Test
    void getTotalRange() {
        // Create timeline and populate it with events
        TimelineEvent testEvent;
        Timeline testTimeline = new Timeline();
        testTimeline.setStartUnit(0);
        testTimeline.setEndUnit(200);

        long[] testPlaces = new long[] {1, 5, 42, 44, 101};

        for (int i = 0; i < 5; i++) {
            testEvent = new TimelineEvent();
            testEvent.setPlaceOnTimeline(testPlaces[i]);
            testTimeline.addEvent(testEvent);
        }

        // Total range should be between 1 and 101 (==100)
        long expected = 100;

        assertEquals(expected, testTimeline.getTotalRange());
    }
}
