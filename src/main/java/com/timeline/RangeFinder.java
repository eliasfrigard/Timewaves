package com.timeline;

/**
 * RangeFinder class. Uses a TimelineEvent Array as a constructor parameter.
 * At the moment it calculates smallest, largest and total range.
 * Might have the possibility to sort and do other calculations later on.
 *
 * This class only works with a TimelineEvent array where all events has a positive placeOnTimeLine property
 * and the events are sorted (except for the totalRange() method that can handle unsorted arrays).
 *
 * @author Fredrik Ljungner fl222ai
 */
public class RangeFinder {
    private TimelineEvent[] events;

    public RangeFinder() {
    }

    public RangeFinder(TimelineEvent[] timelineEventsParam) {
        this.setTimeline(timelineEventsParam);
    }

    public TimelineEvent[] getEvents() {
        return this.events;
    }


    public void setTimeline(TimelineEvent[] timeline) {
        this.events = timeline;
    }

    /**
     * Calculates the smallest range between two TimelineEvents in the TimelineEvent array.
     * @return Smallest range between two TimelineEvents.
     */
    public long smallestRange () {
        long result = 0;

        // Use a big number to compare with
        long smallest = 920000000;

        // Keep track of the current and the previous event
        TimelineEvent previousEvent = null;
        TimelineEvent currentEvent = null;

        // Loop all the events
        for (int i = 0; i < events.length; i++) {

            if (i == 0) {
                previousEvent = events[i];
            } else {
                currentEvent = events[i];

                /* Check if the current event's place on the timeline subtracted by the previous event's
                 place on the timeline is smaller than the current smallest stored value */
                if (currentEvent.getPlaceOnTimeline() - previousEvent.getPlaceOnTimeline() < smallest) {
                    smallest = currentEvent.getPlaceOnTimeline() - previousEvent.getPlaceOnTimeline();
                }

                // Store the current value as a previous value to compare with during the next loop
                previousEvent = currentEvent;
            }
        }
        result = smallest;

        return result;
    }

    /**
     * Calculates the largest range between two TimelineEvents in the TimelineEvent array.
     * @return Largest range between two TimelineEvents.
     */
    public long largestRange () {
        long result = 0;

        // Use a small number to compare with
        long largest = -920000000;

        // Keep track of the current and the previous event
        TimelineEvent previousEvent = null;
        TimelineEvent currentEvent = null;

        // Loop all the events
        for (int i = 0; i < events.length; i++) {

            if (i == 0) {
                previousEvent = events[i];
            } else {
                currentEvent = events[i];

                /* Check if the current event's place on the time line subtracted by the previous event's
                 place on the timeline is larger than the current largest stored value */
                if (currentEvent.getPlaceOnTimeline() - previousEvent.getPlaceOnTimeline() > largest) {
                    largest = currentEvent.getPlaceOnTimeline() - previousEvent.getPlaceOnTimeline();
                }

                // Store the current value as a previous value to compare with during the next loop
                previousEvent = currentEvent;
            }
        }
        result = largest;

        return result;
    }

    /**
     * Calculates the total range between the TimelineEvent with the lowest placeOnTimeline value
     * and the TimelineEvent with the largest placeOnTimeline value in the TimelineEvent array.
     *
     * Work on unsorted arrays of TimelineEvents
     *
     * @return Total range
     */
    public long totalRange () {
        long result = 0;

        // Use a big and a small number to compare with
        long firstPlaceOnTimeLine = 920000000; // Will be the smallest in the end
        long lastPlaceOnTimeLine = 0; // Will be the largest in the end

        // Loop all the events
        for (int i = 0; i < events.length; i++) {

            /*  If the current event's place on the timeline is smaller than the (at first hypothetical)
             * first place - the current event now holds the first place */
            if(events[i].getPlaceOnTimeline() < firstPlaceOnTimeLine) {
                firstPlaceOnTimeLine = events[i].getPlaceOnTimeline();
            }

            /*  If the current event's place on the timeline is larger than the (at first hypothetical)
             * last place - the current event now holds the last place */
            if(events[i].getPlaceOnTimeline() > lastPlaceOnTimeLine) {
                lastPlaceOnTimeLine = events[i].getPlaceOnTimeline();
            }
        }

        // Calculate the result
        result = lastPlaceOnTimeLine - firstPlaceOnTimeLine;

        return result;
    }
}
