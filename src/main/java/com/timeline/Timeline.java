package com.timeline;

import sql.RatingMethods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

/**
 * Timeline class.
 *
 * @author Susanna Persson sp222xw
 */

public class Timeline implements Comparable<Timeline> {
    private int id; // Timeline ID
    private String name; // Name of timeline
    private long startUnit; // Ex: 1850, or 25
    private long endUnit; // Ex: 1996, or 500 000 000
    private String unitName; // Ex: "Year", or "Millisecond"
    private ArrayList<TimelineEvent> events = new ArrayList<>(); // PROTOTYPE ArrayList of event objects in the timeline.
    private String[] keywords;
    private boolean isTODD = false;
    private double rating = -1;
    private TreeMap<Integer, Double> ratings = new TreeMap<>();

    // Constructors

    /**
     * Creates an empty timeline object
     */
    public Timeline() {
    }

    /**
     * Creates a timeline object with set fields, save for the keywords and ID (for example, the timeline is newly created and hasn't been assigned one by the server)
     * @param name name for the timeline. Used for identifying the timeline for the user
     * @param startUnit A number which will be the starting point for the timeline
     * @param endUnit A number which will be the end point for the timeline
     * @param unitName The name for the time axis. For example, it could be millions so that a timeline with startUnit=4 and unitName = "million years" would start at 4 million years
     */
    public Timeline(String name, long startUnit, long endUnit, String unitName) {
        this.setId(-1); // Dummy value for ID that should be overwritten as soon as the timeline is posted to the server
        this.setName(name);
        this.setStartUnit(startUnit);
        this.setEndUnit(endUnit);
        this.setUnitName(unitName);

        String[] kws = new String[1];
        kws[0] = "";

        this.setKeywords(kws);
    }

    /**
     * Creates a timeline object with set fields save for ID (for example when the timeline is created locally and doesn't have an ID from the server yet)
     * @param name name for the timeline. Used for identifying the timeline for the user
     * @param startUnit A number which will be the starting point for the timeline
     * @param endUnit A number which will be the end point for the timeline
     * @param unitName The name for the time axis. For example, it could be millions so that a timeline with startUnit=4 and unitName = "million years" would start at 4 million years
     * @param keywords The keywords for the timeline. Not used yet, but will probably be used for filtering and searching timelines
     */
    public Timeline(String name, long startUnit, long endUnit, String unitName, String[] keywords) {
        this.setId(-1); // Dummy value for ID that should be overwritten as soon as the timeline is posted to the server
        this.setName(name);
        this.setStartUnit(startUnit);
        this.setEndUnit(endUnit);
        this.setUnitName(unitName);
        if (keywords.length > 0) {
            Arrays.sort(keywords); // So that deep equals actually works. Also makes the keywords list a bit more readable
            this.setKeywords(keywords);
        }
    }

    /**
     * Creates a timeline object with set fields, save for the keywords
     * @param id ID for the timeline. Should only be used for lookup against the server
     * @param name name for the timeline. Used for identifying the timeline for the user
     * @param startUnit A number which will be the starting point for the timeline
     * @param endUnit A number which will be the end point for the timeline
     * @param unitName The name for the time axis. For example, it could be millions so that a timeline with startUnit=4 and unitName = "million years" would start at 4 million years
     */
    public Timeline(int id, String name, long startUnit, long endUnit, String unitName) {
        this.setId(id);
        this.setName(name);
        this.setStartUnit(startUnit);
        this.setEndUnit(endUnit);
        this.setUnitName(unitName);

        String[] kws = new String[1];
        kws[0] = "";

        this.setKeywords(kws);
    }

    /**
     * Creates a timeline object with set fields
     * @param id ID for the timeline. Should only be used for lookup against the server
     * @param name name for the timeline. Used for identifying the timeline for the user
     * @param startUnit A number which will be the starting point for the timeline
     * @param endUnit A number which will be the end point for the timeline
     * @param unitName The name for the time axis. For example, it could be millions so that a timeline with startUnit=4 and unitName = "million years" would start at 4 million years
     * @param keywords The keywords for the timeline. Not used yet, but will probably be used for filtering and searching timelines
     */
    public Timeline(int id, String name, long startUnit, long endUnit, String unitName, String[] keywords) {
        this.setId(id);
        this.setName(name);
        this.setStartUnit(startUnit);
        this.setEndUnit(endUnit);
        this.setUnitName(unitName);
        if (keywords.length > 0) {
            Arrays.sort(keywords); // So that deep equals actually works. Also makes the keywords list a bit more readable
            this.setKeywords(keywords);
        }
    }

    // Getters

    /**
     * Gets the ID for the timeline. Mostly used to access it on the database.
     * @return timeline ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the name of the timeline to show to the user to allow them to identify what timeline they're accessing
     * @return name of the timeline
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the starting point of the timeline. This will be combined with the unit name. So if the unit name is year for example, a 0 would mean a start at 0 year
     * @return Starting point value for the timeline
     */
    public long getStartUnit() {
        return startUnit;
    }

    /**
     * Gets the ending point of the timeline. This will be combined with the unit name. So if the unit name is year for example, a 10 would mean an end at 10 year
     * @return Ending point value for the timeline
     */
    public long getEndUnit() {
        return endUnit;
    }

    /**
     * The unit name for the timeline. Could be something like "million years", for example.
     * @return A string for the name of the timelines base unit
     */
    public String getUnitName() {
        return unitName;
    }

    /**
     * Get the array with events.
     * @return An array containing the events on the timeline.
     *
     * @author Victor Ionzon vi222bk.
     */
    public TimelineEvent[] getEvents() {
        TimelineEvent[] arrCopy = new TimelineEvent[events.size()];
        events.toArray(arrCopy);

        return arrCopy;
    }

    /**Returns the number of events in this Timeline.
     * @return int the number of events
     * @author Stefan Jägstrand sj223gg
     * @created 2020-04-22
     */
    public int getNoOfEvents () {
        return events.size();
    }

    public String[] getKeywords() {
        return keywords;
    }

    /**
     * Returns whether this timeline is the timeline of the day
     * @return True if it is, false otherise
     */
    public boolean isTODD() {
        return isTODD;
    }

    // Setters

    protected void setId(int id) {
        this.id = id;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public void setStartUnit(long startUnit) {
        this.startUnit = startUnit;
    }

    public void setEndUnit(long endUnit) {
        this.endUnit = endUnit;
    }

    protected void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    protected void setKeywords(String[] key) {
        this.keywords = Arrays.copyOf(key, key.length);
        Arrays.sort(this.keywords);
    }

    /**
     * Sets whether this timeline is the timeline of the day
     * @param TODD true if it is to be TOD(D), false otherwise
     */
    public void setTODD(boolean TODD) {
        isTODD = TODD;
    }

    // Other methods

    /**
     * Can be used to set the internal event list of the timeline object in one command
     * @param eventList Full list of events for the timeline
     */
    public void setEvents (ArrayList<TimelineEvent> eventList) {
        events = eventList;
    }

    /**Adds passed instance of TimelineEvent
     * to internal list.
     * @author Stefan Jägstrand sj223gg
     * @param newEvent <i>TimelineEvent</i>
     * @created 2020-04-20
     */
    public void addEvent (TimelineEvent newEvent) throws IllegalArgumentException {
        if (newEvent == null) {
            throw new IllegalArgumentException("addEvent() called with null value");
        }
        events.add(newEvent);
        newEvent.setTimeline(this);
    }

    /**
     * Additional addEvent method handling separate params instead of event object.
     * @author Susanna Persson sp222xw
     */

    public void addEvent (String eventName, long placeOnTimeline, String shortDesc, String longDesc, String imagePath) throws IllegalArgumentException {
        if (eventName == null || shortDesc == null || longDesc == null) {
            throw new IllegalArgumentException("addEvent() called with null value");
        }
        TimelineEvent event = new TimelineEvent(this, eventName, placeOnTimeline, shortDesc, longDesc, imagePath);
        events.add(event);
    }

    /**Removes passed instance of TimelineEvent from
     * the Timeline's list of events.
     * Returns <code>true</code> if removed successfully.
     * Returns <code>false</code> if not..
     * @param event
     * @return boolean
     * @author Stefan Jägstrand sj223gg
     * @created 2020-04-21
     */
    public boolean removeEvent (TimelineEvent event) {
        // Remove passed event from arrayList (uses TimelineEvents equals method)
        boolean successful = events.remove(event);

        return successful;
    }

    /**Returns true if passed TimelineEvent exists in
     * the Timeline's list of events.
     * @param event
     * @return boolean
     * @author Stefan Jägstrand sj223gg
     * @created 2020-04-21
     */
    public boolean containsEvent (TimelineEvent event) {
        return events.contains(event);
    }

    /**
     * Gets the range between startUnit and endUnit. May be expanded in the future.
     */
    public long getRange() {
        return this.getEndUnit() - this.getStartUnit();
    }


    /**
     * Calculates the smallest range between two TimelineEvents in the TimelineEvent array.
     * Works only on sorted arrays where all events has a positive placeOnTimeLine property
     *
     * @return Smallest range between two TimelineEvents.
     * @author Fredrik Ljungner fl222ai
     */
    public long getSmallestRange() {
        RangeFinder rangeFinder = new RangeFinder(this.getEvents());
        long result = rangeFinder.smallestRange();

        return result;
    }

    /**
     * Calculates the largest range between two TimelineEvents in the TimelineEvent array.
     *
     * Works only on sorted arrays where all events has a positive placeOnTimeLine property
     *
     * @return Largest range between two TimelineEvents.
     * @author Fredrik Ljungner fl222ai
     */
    public long getLargestRange() {
        RangeFinder rangeFinder = new RangeFinder(this.getEvents());
        long result = rangeFinder.largestRange();

        return result;
    }


    /**
     * Calculates the total range between the TimelineEvent with the lowest placeOnTimeline value
     * and the TimelineEvent with the largest placeOnTimeline value in the TimelineEvent array.
     *
     * Works on sorted and unsorted arrays where all events has a positive placeOnTimeLine property
     *
     * @return Total range
     * @author Fredrik Ljungner fl222ai
     */
    public long getTotalRange() {
        RangeFinder rangeFinder = new RangeFinder(this.getEvents());
        long result = rangeFinder.totalRange();

        return result;
    }

    @Override
    public int compareTo(Timeline timeline) {
        // TODD is priority when sorting, so that the TODD
        // is at the top of the timeline-menu.
        if (this.isTODD()) return -1;       // Added by sj 05-12
        if (timeline.isTODD()) return 1;    // sj 05-12
        return this.getName().compareToIgnoreCase(timeline.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Timeline timeline = (Timeline) o;

        return getName().equals(timeline.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    /**
     * Updates and stores rating of currently shown timeline, digit taken from ratings bar in PrimaryWindow
     * @Elias Holmér
     * @Param Double score
     */
    public double getRating(){
        if (rating == -1) {
            rating = ratings.values().stream().mapToDouble(Double::doubleValue).sum()/ratings.size();
        }
        return this.rating;
    }
    public void setRating(Double score){
        int userID = AppProperties.getUser().getUserID();


        if (ratings.containsKey(userID) && ratings.get(userID) != score) {
            RatingMethods.updateRatingSet(id, userID, score);
        } else if (!ratings.containsKey(userID)) {
            RatingMethods.insertRatingSet(id, userID, score);
        }
        ratings.put(userID,score);
        rating = rating == -1 || ratings.size() == 1 ? score : ratings.values().stream().mapToDouble(Double::doubleValue).sum()/ratings.size();
    }

    /**
     * Puts a key-value pair of the user giving a rating as key and the rating they gave as the value
     * Called from RatingMethods
     * @param userID The user that gave the rat
     * @param rating The rating the user gave this timeline
     */
    public void putRating(int userID, double rating) {
        ratings.put(userID, rating);
    }
}
