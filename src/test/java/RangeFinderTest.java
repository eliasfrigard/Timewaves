import com.timeline.RangeFinder;
import com.timeline.TimelineEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for RangeFinder.
 * @author Susanna Persson sp222xw
 */

class RangeFinderTest {

    private static int count = 0;

    @BeforeEach
    void setUp() {
        count++;
        System.out.println("Running test " + count + "...");
    }

    @AfterEach
    void tearDown() {
        System.out.println("Test " + count + " done.");
    }

    @Test
    void setTimeline() {
        // SETUP:
        // Setup array
        TimelineEvent[] arr = new TimelineEvent[5];
        for (int i = 0; i < 5; i++) {
            TimelineEvent event = new TimelineEvent();
            arr[i] = event;
        }

        // Setup SUT
        RangeFinder sut = new RangeFinder();
        sut.setTimeline(arr);

        // Setup expected variable
        int expected = 5;

        // EXERCISING THE SUT:
        int actual = sut.getEvents().length;

        // COMPARE RESULT:
        assertEquals(expected, actual);
    }

    @Test
    void smallestRange() {
        // SETUP:
        // Setup array
        TimelineEvent[] arr = new TimelineEvent[5];
        long[] places = {1, 10, 15, 44, 99};
        for (int i = 0; i < 5; i++) {
            TimelineEvent event = new TimelineEvent();
            event.setPlaceOnTimeline(places[i]);
            arr[i] = event;
        }

        // Setup SUT
        RangeFinder sut = new RangeFinder();
        sut.setTimeline(arr);

        // Setup expected variable
        long expected = 5;

        // EXERCISING THE SUT:
        long actual = sut.smallestRange();

        // COMPARE RESULT:
        assertEquals(expected, actual);
    }

    @Test
    void largestRange() {
        // SETUP:
        // Setup array
        TimelineEvent[] arr = new TimelineEvent[5];
        long[] places = {1, 10, 15, 44, 99};
        for (int i = 0; i < 5; i++) {
            TimelineEvent event = new TimelineEvent();
            event.setPlaceOnTimeline(places[i]);
            arr[i] = event;
        }

        // Setup SUT
        RangeFinder sut = new RangeFinder();
        sut.setTimeline(arr);

        // Setup expected variable
        long expected = 55;

        // EXERCISING THE SUT:
        long actual = sut.largestRange();

        // COMPARE RESULT:
        assertEquals(expected, actual);
    }

    @Test
    void totalRange() {
        // SETUP:
        // Setup array
        TimelineEvent[] arr = new TimelineEvent[5];
        long[] places = {1, 10, 15, 44, 99};
        for (int i = 0; i < 5; i++) {
            TimelineEvent event = new TimelineEvent();
            event.setPlaceOnTimeline(places[i]);
            arr[i] = event;
        }

        // Setup SUT
        RangeFinder sut = new RangeFinder();
        sut.setTimeline(arr);

        // Setup expected variable
        long expected = 98;

        // EXERCISING THE SUT:
        long actual = sut.totalRange();

        // COMPARE RESULT:
        assertEquals(expected, actual);
    }
}